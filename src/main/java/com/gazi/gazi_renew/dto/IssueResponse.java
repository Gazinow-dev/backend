package com.gazi.gazi_renew.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.gazi.gazi_renew.domain.Issue;
import com.gazi.gazi_renew.domain.Station;
import com.gazi.gazi_renew.domain.enums.IssueKeyword;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class IssueResponse {

    private Long id;
    private String title;
    private String content;
    private String agoTime; // 몇분전, 몇시간전...
    private List<String> lines;
    private int likeCount;
    private boolean isLike;
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
    @Setter
    @Getter
    @Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class IssueSummaryDto{
        private Long id;
        private String title;
        private int likeCount;
        private IssueKeyword keyword;
        private String agoTime; // 몇분전, 몇시간전...


        public static List<IssueSummaryDto> getIssueSummaryDto(List<Issue> issues){
            List<IssueResponse.IssueSummaryDto> issueSummaryDto = issues.stream().map(
                    m ->{
                        IssueResponse.IssueSummaryDto.IssueSummaryDtoBuilder builder = IssueSummaryDto.builder()
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


        public static List<IssueSummaryDto> getIssueSummaryDtoByLine(List<IssueResponse.IssueSummaryDto> issues){
            List<IssueResponse.IssueSummaryDto> issueSummaryDtoList = new ArrayList<>();

            Set<Long> idSet = new HashSet<>();
            // 중복된거면 넣지않기
            for(IssueResponse.IssueSummaryDto issue : issues){
                System.out.println("id: " + issue.getId());
                if (!idSet.contains(issue.getId())) {
                    // HashSet에 아직 존재하지 않는 id인 경우에만 리스트에 추가합니다.
                    issueSummaryDtoList.add(issue);
                    idSet.add(issue.getId());
                }
            }

            return issueSummaryDtoList;
        }
    }
}
