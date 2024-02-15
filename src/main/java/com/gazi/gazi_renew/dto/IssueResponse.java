package com.gazi.gazi_renew.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.gazi.gazi_renew.domain.Issue;
import com.gazi.gazi_renew.domain.Station;
import com.gazi.gazi_renew.domain.enums.IssueKeyword;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class IssueResponse {

    private Long id;
    private String title;
    private String content;
    private String date;
    private String line;
    private int likeCount;
    private IssueKeyword keyword;
    private LocalDateTime startDate;
    private LocalDateTime expireDate;
    private List<StationDto> stationDtos;

    public static StationDto getStation(Station station){
        StationDto stationDto = new StationDto();
        stationDto.line = station.getLine();
        stationDto.stationName = station.getName();
        return stationDto;
    }

    public static List<StationDto> getStations(List<Station> stations){
        System.out.println("역 개수 : " + stations.size());
        List<StationDto> stationDtos = stations.stream()
                .map(station -> getStation(station))
                .collect(Collectors.toList());
        return stationDtos;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class StationDto {
        private String line;
        private String stationName;
    }

    // 이슈 요약
    @Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class IssueSummaryDto{
        private Long id;
        private String title;
        private int likeCount;
        private IssueKeyword keyword;


        public static List<IssueSummaryDto> getIssueSummaryDto(List<Issue> issues){
            List<IssueResponse.IssueSummaryDto> issueSummaryDto = issues.stream().map(
                    m ->{
                        IssueResponse.IssueSummaryDto.IssueSummaryDtoBuilder builder = IssueResponse.IssueSummaryDto.builder()
                                .id(m.getId())
                                .title(m.getTitle())
                                .keyword(m.getKeyword());

                        int likeCount = Optional.ofNullable(m.getLikes())
                                .map(Set::size)
                                .orElse(0);
                        builder.likeCount(likeCount);
                        return builder.build();
                    }
            ).collect(Collectors.toList());
            return issueSummaryDto;
        }
    }
}
