package com.gazi.gazi_renew.issue.controller.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueDetail;
import com.gazi.gazi_renew.issue.infrastructure.IssueEntity;
import com.gazi.gazi_renew.station.infrastructure.StationEntity;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class IssueResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final String agoTime; // 몇분전, 몇시간전...
    private final List<String> lines;
    private final int likeCount;
    private final boolean isLike;
    private final IssueKeyword keyword;
    private final LocalDateTime startDate;
    private final LocalDateTime expireDate;
    private final List<StationDto> stationDtos;
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


        public static List<IssueSummaryDto> getIssueSummaryDto(List<IssueEntity> issueEntities){
            List<IssueResponse.IssueSummaryDto> issueSummaryDto = issueEntities.stream().map(
                    m ->{
                        IssueResponse.IssueSummaryDto.IssueSummaryDtoBuilder builder = IssueSummaryDto.builder()
                                .id(m.getId())
                                .title(m.getTitle())
                                .keyword(m.getKeyword());

                        int likeCount = Optional.ofNullable(m.getLikeEntities())
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
                if (!idSet.contains(issue.getId())) {
                    // HashSet에 아직 존재하지 않는 id인 경우에만 리스트에 추가합니다.
                    issueSummaryDtoList.add(issue);
                    idSet.add(issue.getId());
                }
            }

            return issueSummaryDtoList;
        }
    }
    // 시간 구하기 로직
    public static String getTime(LocalDateTime startTime) {
        System.out.println(startTime);

        LocalDateTime nowDate = LocalDateTime.now();
        Duration duration = Duration.between(startTime, nowDate);
        Long time = duration.getSeconds();
        String formatTime;

        if (time > 60 && time <= 3600) {
            // 분
            time = time / 60;
            formatTime = time + "분 전";
        } else if (time > 3600 && time <= 86400) {
            time = time / (60 * 60);
            formatTime = time + "시간 전";
        } else if (time > 86400) {
            time = time / 86400;
            formatTime = time + "일 전";
        } else {
            formatTime = time + "초 전";
        }

        return formatTime;
    }
    public static StationDto getStation(StationEntity stationEntity){
        StationDto stationDto = new StationDto();
        stationDto.line = stationEntity.getLine();
        stationDto.stationName = stationEntity.getName();
        return stationDto;
    }

    public static List<StationDto> getStations(List<StationEntity> stationEntities){
        System.out.println("역 개수 : " + stationEntities.size());
        List<StationDto> stationDtos = stationEntities.stream()
                .map(station -> getStation(station))
                .collect(Collectors.toList());
        return stationDtos;
    }
    public static IssueResponse from(Issue issue) {
        return IssueResponse.builder()
                .id(issue.getId())
                .title(issue.getTitle())
                .content(issue.getContent())
                .agoTime(getTime(issue.getStartDate()))
                .lines(issue.getLines())
                .likeCount(issue.getLikeCount())
                .startDate(issue.getStartDate())
                .expireDate(issue.getExpireDate())
                .stationDtos(getStations(issue.getIssueStations().stream().map(::from)))
                .build();
    }
    public static IssueResponse fromIssueDetail(IssueDetail issueDetail) {
        return IssueResponse.builder()
                .id(issueDetail.getIssue().getId())
                .title(issueDetail.getIssue().getTitle())
                .content(issueDetail.getIssue().getContent())
                .agoTime(getTime(issueDetail.getIssue().getStartDate()))
                .isLike(issueDetail.isLike())
                .lines(issueDetail.getIssue().getLines())
                .likeCount(issueDetail.getIssue().getLikeCount())
                .startDate(issueDetail.getIssue().getStartDate())
                .expireDate(issueDetail.getIssue().getExpireDate())
                .stationDtos(getStations(issueDetail.getIssue().getIssueStations().stream().map(::from)))
                .build();
    }
    public static Page<IssueResponse> fromIssuePage(Page<Issue> issuePage) {
        return issuePage.map(issue -> {
            IssueResponse.builder()
                    .id(issue.getId())
                    .title(issue.getTitle())
                    .content(issue.getContent())
                    .keyword(issue.getKeyword())
                    .stationDtos(getStations(issue.getStationEntities()))
                    .isLike(issue.isLike())
                    .lines(issue.getLines())
                    .startDate(issue.getStartDate())
                    .expireDate(issue.getExpireDate())
                    .agoTime(getTime(issue.getStartDate()));
                    .build();

        });
    }

}
