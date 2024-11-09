package com.gazi.gazi_renew.issue.controller.response;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueDetail;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Builder
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
    public static class StationDto {
        private final String line;
        private final String stationName;
        @Builder
        public StationDto(String line, String stationName) {
            this.line = line;
            this.stationName = stationName;
        }
    }

    // 이슈 요약
    @Getter
    @Builder
    public static class IssueSummaryDto{
        private Long id;
        private String title;
        private int likeCount;
        private IssueKeyword keyword;
        private String agoTime; // 몇분전, 몇시간전...


        public static List<IssueSummaryDto> getIssueSummaryDto(List<Issue> issueList){
            List<IssueResponse.IssueSummaryDto> issueSummaryDto = issueList.stream().map(
                    m ->{
                        return IssueSummaryDto.builder()
                                .id(m.getId())
                                .title(m.getTitle())
                                .likeCount(m.getLikeCount())
                                .keyword(m.getKeyword())
                                .build();
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
    public static IssueResponse from(Issue issue) {
        List<StationDto> stationDtoList = issue.getStationList().stream().map(issueStation -> {
                return StationDto.builder()
                    .line(issueStation.getLine())
                    .stationName(issueStation.getName())
                    .build();
        }).collect(Collectors.toList());

        return IssueResponse.builder()
                .id(issue.getId())
                .title(issue.getTitle())
                .content(issue.getContent())
                .agoTime(getTime(issue.getStartDate()))
                .lines(issue.getLines())
                .likeCount(issue.getLikeCount())
                .startDate(issue.getStartDate())
                .expireDate(issue.getExpireDate())
                .stationDtos(stationDtoList)
                .build();
    }
    public static IssueResponse fromIssueDetail(IssueDetail issueDetail) {
        List<StationDto> stationDtoList = issueDetail.getIssue().getStationList().stream().map(issueStation -> {
            return StationDto.builder()
                    .line(issueStation.getLine())
                    .stationName(issueStation.getName())
                    .build();
        }).collect(Collectors.toList());

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
                .stationDtos(stationDtoList)
                .build();
    }
    public static Page<IssueResponse> fromIssueDetailPage(Page<Issue> issuePage) {
        Page<IssueResponse> issueResponsePage = new PageImpl<>(
                issuePage.stream().map(issue -> {

                    List<StationDto> stationDtoList = issue.getStationList().stream().map(issueStation -> {
                        return StationDto.builder()
                                .line(issueStation.getLine())
                                .stationName(issueStation.getName())
                                .build();
                    }).collect(Collectors.toList());

                    return IssueResponse.builder()
                            .id(issue.getId())
                            .title(issue.getTitle())
                            .content(issue.getContent())
                            .keyword(issue.getKeyword())
                            .stationDtos(stationDtoList)
                            .lines(issue.getLines())
                            .startDate(issue.getStartDate())
                            .expireDate(issue.getExpireDate())
                            .agoTime(getTime(issue.getStartDate()))
                            .build();

                }).collect(Collectors.toList()),
                issuePage.getPageable(),
                issuePage.getTotalElements()
        );

        return issueResponsePage;

    }

}
