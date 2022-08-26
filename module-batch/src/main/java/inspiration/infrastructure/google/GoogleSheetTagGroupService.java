package inspiration.infrastructure.google;

import inspiration.application.tag.TagGroup;
import inspiration.application.tag.TagGroupService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("ClassCanBeRecord")
public class GoogleSheetTagGroupService implements TagGroupService {
    private final RestTemplate googleApiRestTemplate;

    @Value("${ygtang.google.sheet-id.tag-group}")
    private String tagGroupSheetId;

    @Override
    public List<TagGroup> getTagGroups() {
        ResponseEntity<GoogleSheetResponse> responseEntity = googleApiRestTemplate.getForEntity(
                UriComponentsBuilder.newInstance()
                                    .scheme("https")
                                    .host("sheets.googleapis.com")
                                    .path("/v4/spreadsheets/{spreadsheetId}")
                                    .queryParam("ranges", "A1:J1000")
                                    .queryParam("includeGridData", "true")
                                    .build(tagGroupSheetId),
                GoogleSheetResponse.class
        );
        if (responseEntity.getBody() == null) {
            throw new IllegalStateException("Failed to get tag groups from Google Sheet Api");
        }
        return responseEntity.getBody().getSheets().get(0).getData().get(0).getRowData()
                             .stream()
                             .map(it -> TagGroup.from(
                                     it.getValues()
                                       .stream()
                                       .map(ValuesResponse::getFormattedValue)
                                       .filter(StringUtils::hasText)
                                       .collect(Collectors.toList())
                                     )
                             )
                             .collect(Collectors.toList());
    }

    @Data
    static class GoogleSheetResponse {
        List<SheetsResponse> sheets;
    }

    @Data
    static class SheetsResponse {
        List<DataResponse> data;
    }

    @Data
    static class DataResponse {
        List<RowDataResponse> rowData;
    }

    @Data
    static class RowDataResponse {
        List<ValuesResponse> values;
    }

    @Data
    static class ValuesResponse {
        String formattedValue;
    }
}

