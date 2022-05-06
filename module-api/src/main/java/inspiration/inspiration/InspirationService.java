package inspiration.inspiration;

import inspiration.aws.AwsS3Service;
import inspiration.exception.NoAccessAuthorizationException;
import inspiration.exception.ResourceNotFoundException;
import inspiration.inspiration.request.InspirationAddRequest;
import inspiration.inspiration.request.InspirationModifyRequest;
import inspiration.inspiration.request.InspirationTagRequest;
import inspiration.inspiration.response.InspirationResponse;
import inspiration.inspiration_tag.InspirationTag;
import inspiration.inspiration_tag.InspirationTagRepository;
import inspiration.member.Member;
import inspiration.member.MemberService;
import inspiration.tag.Tag;
import inspiration.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@CacheConfig(cacheNames = "findInspirations")
@RequiredArgsConstructor
@Service
@Transactional
public class InspirationService {

    private final InspirationRepository inspirationRepository;
    private final TagRepository tagRepository;
    private final InspirationTagRepository inspirationTagRepository;
    private final AwsS3Service awsS3Service;
    private final MemberService memberService;

    @Cacheable(value = "findInspirations")
    @Transactional(readOnly = true)
    public Page<InspirationResponse> findInspirations(Pageable pageable, Long memberId) {

        Member member = memberService.findById(memberId);

        Page<Inspiration> inspirationPage = inspirationRepository.findAllByIsDeletedAndMember(false, member, pageable);
        inspirationPage.stream()
                .forEach(inspiration -> inspiration.setFilePath(getFilePath(inspiration.getType(), inspiration.getContent())));
        return inspirationPage.map(InspirationResponse::from);
    }

    @CacheEvict(allEntries = true)
    public Long addInspiration(InspirationAddRequest request, Long memberId) {

        Member member = memberService.findById(memberId);
        Inspiration inspiration = request.toEntity();
        inspiration.writeBy(member);
        if (request.getType() == InspirationType.IMAGE) {
            if (request.getFile() == null) {
                throw new IllegalArgumentException("IMAGE 타입은 파일을 업로드 해야합니다.");
            }
            fileUpload(inspiration, Arrays.asList(request.getFile()));
        }
        return inspirationRepository.save(inspiration).getId();
    }

    @CacheEvict(allEntries = true)
    public Long modifyMemo(InspirationModifyRequest request, Long memberId) {

        Inspiration inspiration = inspirationRepository.findById(request.getId())
                .orElseThrow(ResourceNotFoundException::new);

        if (!inspiration.getMember().isSameMember(memberId)) {
            throw new NoAccessAuthorizationException();
        }
        inspiration.modifyMemo(request.getMemo());
        return inspiration.getId();
    }

    @CacheEvict(allEntries = true)
    public void removeInspiration(Long id, Long memberId) {
        Inspiration inspiration = inspirationRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        if (!inspiration.getMember().isSameMember(memberId)) {
            throw new NoAccessAuthorizationException();
        }

        inspiration.remove();
    }

    public Long tagInspiration(InspirationTagRequest request, Long memberId) {

        Inspiration inspiration = inspirationRepository.findById(request.getId())
                .orElseThrow(ResourceNotFoundException::new);

        if (!inspiration.getMember().isSameMember(memberId)) {
            throw new NoAccessAuthorizationException();
        }

        Tag tag = tagRepository.findById(request.getTagId())
                .orElseThrow(ResourceNotFoundException::new);

        if (!tag.getMember().isSameMember(memberId)) {
            throw new NoAccessAuthorizationException();
        }

        inspirationTagRepository.save(new InspirationTag(inspiration, tag));
        return inspiration.getId();
    }

    private void fileUpload(Inspiration inspiration, List<MultipartFile> multipartFiles) {
        List<String> fileNames = awsS3Service.uploadFile(multipartFiles);
        if (!fileNames.isEmpty()) {
            inspiration.setFilePath(fileNames.get(0));
        }
    }

    private String getFilePath(InspirationType type, String content) {
        if (type == InspirationType.IMAGE) {
            return awsS3Service.getFilePath(content);
        }
        return content;
    }

}
