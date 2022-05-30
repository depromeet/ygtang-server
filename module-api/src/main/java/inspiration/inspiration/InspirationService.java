package inspiration.inspiration;

import com.github.siyoon210.ogparser4j.OgParser;
import com.github.siyoon210.ogparser4j.OpenGraph;
import inspiration.RestPage;
import inspiration.aws.AwsS3Service;
import inspiration.exception.NoAccessAuthorizationException;
import inspiration.exception.ResourceNotFoundException;
import inspiration.inspiration.request.InspirationAddRequest;
import inspiration.inspiration.request.InspirationModifyRequest;
import inspiration.inspiration.request.InspirationTagRequest;
import inspiration.inspiration.response.InspirationResponse;
import inspiration.inspiration.response.OpenGraphResponse;
import inspiration.inspiration_tag.InspirationTag;
import inspiration.inspiration_tag.InspirationTagService;
import inspiration.member.Member;
import inspiration.member.MemberService;
import inspiration.tag.Tag;
import inspiration.tag.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class InspirationService {

    private final InspirationRepository inspirationRepository;
    private final AwsS3Service awsS3Service;
    private final MemberService memberService;
    private final TagService tagService;
    private final InspirationTagService inspirationTagService;

    @Transactional(readOnly = true)
    @Cacheable(value = "inspiration", key = "{#memberId + #pageable.pageNumber + #pageable.pageSize}")
    public RestPage<InspirationResponse> findInspirations(Pageable pageable, Long memberId) {

        Member member = memberService.findById(memberId);

        Page<Inspiration> inspirationPage = inspirationRepository.findAllByMember(member, pageable);
        inspirationPage
                .forEach(
                        inspiration ->
                                inspiration.setFilePath(getFilePath(inspiration.getType(), inspiration.getContent())));
        return new RestPage<>(inspirationPage.map(inspiration -> InspirationResponse.of(inspiration, getOG(inspiration.getType() ,inspiration.getContent()))));
    }

    @Transactional(readOnly = true)
    public InspirationResponse findInspiration(Long id, Long memberId) {

        Member member = memberService.findById(memberId);

        Inspiration inspiration = inspirationRepository.findAllByMemberAndId(member, id)
                                                        .orElseThrow(ResourceNotFoundException::new);

        inspiration.setFilePath(getFilePath(inspiration.getType(), inspiration.getContent()));
        return InspirationResponse.of(inspiration, getOG(inspiration.getType(), inspiration.getContent()));
    }

    private OpenGraphResponse getOG(InspirationType inspirationType, String link)  {

        if(inspirationType != InspirationType.LINK){
            return OpenGraphResponse.from(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        try {
            link = URLDecoder.decode(link, "UTF-8");

            String image = getOGContent("image", link);
            String siteName = getOGContent("content", link);
            String title = getOGContent("title", link);
            String url = getOGContent("url", link);
            String description = getOGContent("description", link);
            return OpenGraphResponse.of(HttpStatus.OK.value(), image, siteName, title, url, description);

        } catch (Exception e) {
            return OpenGraphResponse.from(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private String getOGContent(String content, String link) {

        try {
            OgParser ogParser = new OgParser();
            OpenGraph openGraph = ogParser.getOpenGraphOf(link);

            if (content.equals("image")) {
                return openGraph.getContentOf(content, 0).getValue();
            }
            return  openGraph.getContentOf(content).getValue();
        } catch (Exception e) {
            return null;
        }
    }

    public OpenGraphResponse getOG(String link) {
        return getOG(InspirationType.LINK, link);
    }

    @CacheEvict(value = "inspiration", allEntries = true)
    public Long addInspiration(InspirationAddRequest request,  Long memberId) {

        Member member = memberService.findById(memberId);

        Inspiration tmpInspiration = request.toEntity();
        tmpInspiration.writeBy(member);

        if (request.getType() == InspirationType.IMAGE) {
            if(request.getFile() == null){
                throw new IllegalArgumentException("IMAGE 타입은 파일을 업로드 해야합니다.");
            }
            fileUpload(tmpInspiration, List.of(request.getFile()));
        }
        Inspiration inspiration = inspirationRepository.save(tmpInspiration);

        if (request.getTagIds() != null) {
            List<Tag> tags = request.getTagIds().stream()
                    .map(tagService::getTag)
                    .collect(Collectors.toList());
            tags.forEach(tag -> inspirationTagService.save(InspirationTag.of(inspiration, tag)));
        }
        return inspiration.getId();
    }

    @Transactional(readOnly = true)
    public RestPage<InspirationResponse> findInspirationsByTags(Pageable pageable, List<Long> tagIds, Long memberId) {

        Member member = memberService.findById(memberId);
        List<Tag> tags = tagIds.stream()
                                .map(tagService::getTag)
                                .collect(Collectors.toList());

        List<Inspiration> inspirations = inspirationRepository.findDistinctInspirationByTags(tags, Long.parseLong(String.valueOf(tags.size())))
                                                                .orElseThrow(ResourceNotFoundException::new);

        List<Long> inspirationIds = inspirations.stream()
                                                .map(Inspiration::getId)
                                                .collect(Collectors.toList());

        Page<Inspiration> inspirationPage = inspirationRepository.findAllByIdIn(inspirationIds, pageable);
        inspirationPage
                .forEach(
                        inspiration ->
                                inspiration.setFilePath(getFilePath(inspiration.getType(), inspiration.getContent())));
        return new RestPage<>(inspirationPage.map(inspiration -> InspirationResponse.of(inspiration, getOG(inspiration.getType() ,inspiration.getContent()))));
    }


    @CacheEvict(value = "inspiration", allEntries = true)
    public Long modifyMemo(InspirationModifyRequest request, Long memberId) {

        Inspiration inspiration = getInspiration(request.getId());

        if (!inspiration.getMember().isSameMember(memberId)) {
            throw new NoAccessAuthorizationException();
        }
        inspiration.modifyMemo(request.getMemo());
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
    }

    @Transactional
    @CacheEvict(value = "inspiration", allEntries = true)
    public void removeAllInspiration(Long memberId) {
        Member member = memberService.findById(memberId);

        List<Inspiration> inspirations = inspirationRepository.findAllByMember(member);

        inspirationTagService.deleteAllByInspirationIn(inspirations);

        inspirationRepository.deleteAllByMember(member);

    }

    @CacheEvict(value = "inspiration", allEntries = true)
    public Long tagInspiration(InspirationTagRequest request, Long memberId) {

        Inspiration inspiration = getInspiration(request.getId());

        if (!inspiration.getMember().isSameMember(memberId)) {
            throw new NoAccessAuthorizationException();
        }

        Tag tag = tagService.getTag(request.getTagId());

        if (!tag.getMember().isSameMember(memberId)) {
            throw new NoAccessAuthorizationException();
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
        inspirationTagService.delete(inspirationTag);
    }

    @CacheEvict(value = "inspiration", allEntries = true)
    public void unTagInspirationByInspiration(Long id, Long memberId) {

        if(!getInspiration(id).getMember().isSameMember(memberId)) {
            throw new NoAccessAuthorizationException();
        }

        Inspiration inspiration = getInspiration(id);

        inspirationTagService.deleteAllByInspiration(inspiration);
    }

    private void fileUpload(Inspiration inspiration, List<MultipartFile> multipartFiles) {
        List<String> fileNames = awsS3Service.uploadFile(multipartFiles);
        if(!fileNames.isEmpty()){
            inspiration.setFilePath(fileNames.get(0));
        }
    }

    private String getFilePath(InspirationType type, String content) {
        if(type == InspirationType.IMAGE){
            return awsS3Service.getFilePath(content);
        }
        return content;
    }

    private Inspiration getInspiration(Long id) {
        return inspirationRepository.findById(id)
                                    .orElseThrow(ResourceNotFoundException::new);
    }
}
