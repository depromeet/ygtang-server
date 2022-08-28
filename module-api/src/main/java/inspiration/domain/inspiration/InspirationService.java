package inspiration.domain.inspiration;

import inspiration.aws.AwsS3Service;
import inspiration.domain.inspiration.request.InspirationAddRequestVo;
import inspiration.domain.inspiration.request.InspirationModifyRequestVo;
import inspiration.domain.inspiration.request.InspirationTagRequestVo;
import inspiration.domain.inspiration.response.InspirationResponseVo;
import inspiration.domain.inspiration_tag.InspirationTag;
import inspiration.domain.inspiration_tag.InspirationTagRepository;
import inspiration.domain.inspiration_tag.InspirationTagService;
import inspiration.domain.member.Member;
import inspiration.domain.member.MemberService;
import inspiration.domain.tag.Tag;
import inspiration.domain.tag.TagRepository;
import inspiration.domain.tag.TagService;
import inspiration.exception.ConflictRequestException;
import inspiration.exception.NoAccessAuthorizationException;
import inspiration.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
@SuppressWarnings("ClassCanBeRecord")
public class InspirationService {

    private final InspirationRepository inspirationRepository;
    private final AwsS3Service awsS3Service;
    private final MemberService memberService;
    private final TagService tagService;
    private final InspirationTagService inspirationTagService;
    private final InspirationTagRepository inspirationTagRepository;
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "inspiration", key = "{#memberId, #pageable.pageNumber, #pageable.pageSize}")
    public Page<InspirationResponseVo> findInspirations(Pageable pageable, Long memberId) {
        Member member = memberService.findById(memberId);
        return inspirationRepository.findAllByMember(member, pageable)
                                    .map(it -> InspirationResponseVo.of(it, awsS3Service));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "inspiration", key = "{#memberId, #id}")
    public InspirationResponseVo findInspiration(Long id, Long memberId) {
        Member member = memberService.findById(memberId);
        Inspiration inspiration = inspirationRepository.findAllByMemberAndId(member, id)
                                                       .orElseThrow(ResourceNotFoundException::new);
        return InspirationResponseVo.of(inspiration, awsS3Service);
    }

    @CacheEvict(value = "inspiration", allEntries = true)
    public Long addInspiration(InspirationAddRequestVo requestVo, Long memberId) {

        Member member = memberService.findById(memberId);

        Inspiration tmpInspiration = requestVo.toEntity();
        tmpInspiration.writeBy(member);

        if (requestVo.getType() == InspirationType.IMAGE) {
            if (requestVo.getFile() == null) {
                throw new IllegalArgumentException("IMAGE 타입은 파일을 업로드 해야합니다.");
            }
            fileUpload(tmpInspiration, List.of(requestVo.getFile()));
        }
        Inspiration inspiration = inspirationRepository.save(tmpInspiration);

        if (requestVo.getTagIds() != null) {
            List<Tag> tags = requestVo.getTagIds().stream()
                                      .map(tagService::getTag)
                                      .collect(Collectors.toList());
            tags.forEach(tag -> inspirationTagService.save(InspirationTag.of(inspiration, tag)));
        }
        return inspiration.getId();
    }

    @Transactional(readOnly = true)
    public Page<InspirationResponseVo> findInspirationsByTags(
            List<Long> tagIds,
            List<InspirationType> types,
            LocalDateTime createdDateTimeFrom,
            LocalDateTime createdDateTimeTo,
            Long memberId,
            Pageable pageable
    ) {
        return inspirationRepository.findDistinctByMemberIdAndTagIdInAndTypeAndCreatedDateTimeBetween(
                memberId,
                tagIds,
                types,
                createdDateTimeFrom,
                createdDateTimeTo,
                pageable
        ).map(it -> InspirationResponseVo.of(it, awsS3Service));
    }


    @CacheEvict(value = "inspiration", allEntries = true)
    public Long modifyMemo(InspirationModifyRequestVo requestVo, Long memberId) {
        Inspiration inspiration = getInspiration(requestVo.getId());
        if (!inspiration.getMember().isSameMember(memberId)) {
            throw new NoAccessAuthorizationException();
        }
        inspiration.modifyMemo(requestVo.getMemo());
        return inspiration.getId();
    }

    @Transactional
    @CacheEvict(value = "inspiration", allEntries = true)
    public void removeInspiration(Long id, Long memberId) {
        Inspiration inspiration = getInspiration(id);

        if (!inspiration.getMember().isSameMember(memberId)) {
            throw new NoAccessAuthorizationException();
        }

        inspirationRepository.delete(inspiration);

        if (inspiration.hasFile()) {
            String filename = inspiration.getContent();
            try {
                awsS3Service.deleteFile(filename);
            } catch (Exception e) {
                log.error("Failed to delete file. filename: {}", filename, e);
            }
        }
    }

    @Transactional
    @CacheEvict(value = "inspiration", allEntries = true)
    public void removeAllInspiration(Long memberId) {
        Member member = memberService.findById(memberId);

        List<Inspiration> inspirations = inspirationRepository.findAllByMember(member);

        inspirationTagService.deleteAllByInspirationIn(inspirations);

        inspirationRepository.deleteAllByMember(member);

        tagRepository.deleteAllByMember(member);

        inspirations.stream()
                    .filter(Inspiration::hasFile)
                    .map(Inspiration::getContent)
                    .forEach(filename -> {
                        try {
                            awsS3Service.deleteFile(filename);
                        } catch (Exception e) {
                            log.error("Failed to delete file. filename: {}", filename, e);
                        }
                    });

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @CacheEvict(value = "inspiration", allEntries = true)
    public Long tagInspiration(InspirationTagRequestVo requestVo, Long memberId) {

        Inspiration inspiration = getInspiration(requestVo.getInspirationId());

        if (!inspiration.getMember().isSameMember(memberId)) {
            throw new NoAccessAuthorizationException();
        }

        Tag tag = tagService.getTag(requestVo.getTagId());

        if (!tag.getMember().isSameMember(memberId)) {
            throw new NoAccessAuthorizationException();
        }
        if (inspirationTagRepository.findByInspirationAndTag(inspiration, tag).isPresent()) {
            throw new ConflictRequestException();
        }
        inspirationTagService.save(new InspirationTag(inspiration, tag));
        return inspiration.getId();
    }

    @CacheEvict(value = "inspiration", allEntries = true)
    public void unTagInspiration(Long id, Long tagId, Long memberId) {

        Inspiration inspiration = getInspiration(id);

        if (!inspiration.getMember().isSameMember(memberId)) {
            throw new NoAccessAuthorizationException();
        }

        Tag tag = tagService.getTag(tagId);

        if (!tag.getMember().isSameMember(memberId)) {
            throw new NoAccessAuthorizationException();
        }

        InspirationTag inspirationTag = inspirationTagService.findInspirationTag(inspiration, tag);
        inspirationTagRepository.delete(inspirationTag);

    }

    @CacheEvict(value = "inspiration", allEntries = true)
    public void unTagInspirationByInspiration(Long id, Long memberId) {

        if (!getInspiration(id).getMember().isSameMember(memberId)) {
            throw new NoAccessAuthorizationException();
        }

        Inspiration inspiration = getInspiration(id);

        inspirationTagService.deleteAllByInspiration(inspiration);
    }

    @CacheEvict(value = "inspiration", allEntries = true)
    public void unTagInspirationByTag(Long tagId, Long memberId) {

        Tag tag = tagService.getTag(tagId);

        if (!tag.getMember().isSameMember(memberId)) {
            throw new NoAccessAuthorizationException();
        }

        inspirationTagService.deleteAllByTag(tag);

    }

    private void fileUpload(Inspiration inspiration, List<MultipartFile> multipartFiles) {
        List<String> fileNames = awsS3Service.uploadFile(multipartFiles);
        if (!fileNames.isEmpty()) {
            inspiration.setFilePath(fileNames.get(0));
        }
    }

    private Inspiration getInspiration(Long id) {
        return inspirationRepository.findById(id)
                                    .orElseThrow(ResourceNotFoundException::new);
    }
}
