package com.gazi.gazi_renew.issue.controller.response;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.dto.IssueStationDetail;
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
    private final int commentCount;
    private final boolean isLike;
    private final boolean isCommentRestricted;
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
    public static IssueResponse fromIssueDetail(IssueStationDetail issueStationDetail) {
        List<StationDto> stationDtoList = issueStationDetail.getStationList().stream().map(issueStation -> {
            return StationDto.builder()
                    .line(issueStation.getLine())
                    .stationName(issueStation.getName())
                    .build();
        }).collect(Collectors.toList());

        return IssueResponse.builder()
                .id(issueStationDetail.getIssue().getId())
                .title(issueStationDetail.getIssue().getTitle())
                .content(issueStationDetail.getIssue().getContent())
                .keyword(issueStationDetail.getIssue().getKeyword())
                .agoTime(getTime(issueStationDetail.getIssue().getStartDate()))
                .stationDtos(stationDtoList)
                .lines(issueStationDetail.getLineList().stream()
                        .map(Line::getLineName)
                        .collect(Collectors.toList()))
                .likeCount(issueStationDetail.getIssue().getLikeCount())
                .commentCount(issueStationDetail.getCommentCount())
                .startDate(issueStationDetail.getIssue().getStartDate())
                .expireDate(issueStationDetail.getIssue().getExpireDate())
                .isLike(issueStationDetail.isLike())
                .isCommentRestricted(issueStationDetail.isCommentRestricted())
                .build();
    }
    public static IssueResponse fromPopularIssueDetail(IssueStationDetail issueStationDetail) {
        List<StationDto> stationDtoList = issueStationDetail.getStationList().stream().map(issueStation -> {
            return StationDto.builder()
                    .line(issueStation.getLine())
                    .stationName(issueStation.getName())
                    .build();
        }).collect(Collectors.toList());

        return IssueResponse.builder()
                .id(issueStationDetail.getIssue().getId())
                .title(issueStationDetail.getIssue().getTitle())
                .content(issueStationDetail.getIssue().getContent())
                .keyword(issueStationDetail.getIssue().getKeyword())
                .agoTime(getTime(issueStationDetail.getIssue().getStartDate()))
                .stationDtos(stationDtoList)
                .lines(issueStationDetail.getLineList().stream()
                        .map(Line::getLineName)
                        .collect(Collectors.toList()))
                .likeCount(issueStationDetail.getIssue().getLikeCount())
                .commentCount(issueStationDetail.getCommentCount())
                .startDate(issueStationDetail.getIssue().getStartDate())
                .expireDate(issueStationDetail.getIssue().getExpireDate())
                .isLike(issueStationDetail.isLike())
                .build();
    }
    public static Page<IssueResponse> fromIssueDetailPage(Page<IssueStationDetail> issueStationDetails) {
        Page<IssueResponse> issueResponsePage = new PageImpl<>(
                issueStationDetails.stream().map(issueStationDetail -> {
                    List<StationDto> stationDtoList = issueStationDetail.getStationList().stream().map(issueStation -> {
                        return StationDto.builder()
                                .line(issueStation.getLine())
                                .stationName(issueStation.getName())
                                .build();
                    }).collect(Collectors.toList());
                    Issue issue = issueStationDetail.getIssue();
                    return IssueResponse.builder()
                            .id(issue.getId())
                            .title(issue.getTitle())
                            .content(issue.getContent())
                            .keyword(issue.getKeyword())
                            .stationDtos(stationDtoList)
                            .lines(issueStationDetail.getLineList().stream()
                                    .map(Line::getLineName)
                                    .collect(Collectors.toList()))
                            .likeCount(issueStationDetail.getIssue().getLikeCount())
                            .commentCount(issueStationDetail.getCommentCount())
                            .startDate(issue.getStartDate())
                            .expireDate(issue.getExpireDate())
                            .agoTime(getTime(issue.getStartDate()))
                            .isLike(issueStationDetail.isLike())
                            .build();

                }).collect(Collectors.toList()),
                issueStationDetails.getPageable(),
                issueStationDetails.getTotalElements()
        );

        return issueResponsePage;

    }

}
