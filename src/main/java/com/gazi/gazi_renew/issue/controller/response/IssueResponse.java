package com.gazi.gazi_renew.issue.controller.response;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.dto.IssueDetail;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.station.domain.Line;
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
    @Getter
    public static class StationDto {
        private final String line;
        private final String stationName;
        @Builder
        public StationDto(String line, String stationName) {
            this.line = line;
            this.stationName = stationName;
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
                .lines(issue.getLines().stream()
                        .map(Line::getLineName)
                        .collect(Collectors.toList()))
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
                .lines(issueDetail.getIssue().getLines().stream()
                        .map(Line::getLineName)
                        .collect(Collectors.toList()))
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
                            .lines(issue.getLines().stream()
                                    .map(Line::getLineName)
                                    .collect(Collectors.toList()))
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
