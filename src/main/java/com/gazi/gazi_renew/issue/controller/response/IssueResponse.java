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
    public static IssueResponse fromIssueDetail(List<IssueStationDetail> issueStationDetails) {

        Map<Long, List<IssueStationDetail>> groupedById = issueStationDetails.stream()
                .collect(Collectors.groupingBy(
                        IssueStationDetail::getId,
                        LinkedHashMap::new, // 순서를 유지하기 위해 LinkedHashMap 사용
                        Collectors.toList()
                ));
        // Grouped 데이터를 IssueResponse로 변환
        List<IssueResponse> issueResponses = groupedById.values().stream()
                .map(details -> {
                    // 동일한 `id`의 IssueStationDetail 데이터를 처리
                    IssueStationDetail firstDetail = details.get(0); // 대표 데이터

                    // StationDto 리스트 생성
                    List<StationDto> stationDtoList = details.stream()
                            .map(detail -> StationDto.builder()
                                    .line(detail.getLine())
                                    .stationName(detail.getStationName())
                                    .build())
                            .collect(Collectors.toList());

                    // Line 리스트 생성
                    List<String> lineList = details.stream()
                            .map(IssueStationDetail::getLine)
                            .distinct()
                            .collect(Collectors.toList());

                    // IssueResponse 생성
                    return IssueResponse.builder()
                            .id(firstDetail.getId())
                            .title(firstDetail.getTitle())
                            .content(firstDetail.getContent())
                            .keyword(firstDetail.getKeyword())
                            .agoTime(getTime(firstDetail.getStartDate()))
                            .stationDtos(stationDtoList)
                            .lines(lineList)
                            .likeCount(firstDetail.getLikeCount())
                            .commentCount(firstDetail.getCommentCount())
                            .startDate(firstDetail.getStartDate())
                            .expireDate(firstDetail.getExpireDate())
                            .isLike(firstDetail.isLike())
                            .isCommentRestricted(firstDetail.isCommentRestricted())
                            .build();
                })
                .collect(Collectors.toList());
        return issueResponses.get(0);
    }
    public static List<IssueResponse> fromPopularIssueDetail(List<IssueStationDetail> issueStationDetails) {

        Map<Long, List<IssueStationDetail>> groupedById = issueStationDetails.stream()
                .collect(Collectors.groupingBy(
                        IssueStationDetail::getId,
                        LinkedHashMap::new, // 순서를 유지하기 위해 LinkedHashMap 사용
                        Collectors.toList()
                ));
        // Grouped 데이터를 IssueResponse로 변환
        List<IssueResponse> issueResponses = groupedById.values().stream()
                .map(details -> {
                    // 동일한 `id`의 IssueStationDetail 데이터를 처리
                    IssueStationDetail firstDetail = details.get(0); // 대표 데이터

                    // StationDto 리스트 생성
                    List<StationDto> stationDtoList = details.stream()
                            .map(detail -> StationDto.builder()
                                    .line(detail.getLine())
                                    .stationName(detail.getStationName())
                                    .build())
                            .collect(Collectors.toList());

                    // Line 리스트 생성
                    List<String> lineList = details.stream()
                            .map(IssueStationDetail::getLine)
                            .distinct()
                            .collect(Collectors.toList());

                    // IssueResponse 생성
                    return IssueResponse.builder()
                            .id(firstDetail.getId())
                            .title(firstDetail.getTitle())
                            .content(firstDetail.getContent())
                            .keyword(firstDetail.getKeyword())
                            .agoTime(getTime(firstDetail.getStartDate()))
                            .stationDtos(stationDtoList)
                            .lines(lineList)
                            .likeCount(firstDetail.getLikeCount())
                            .commentCount(firstDetail.getCommentCount())
                            .startDate(firstDetail.getStartDate())
                            .expireDate(firstDetail.getExpireDate())
                            .isLike(firstDetail.isLike())
                            .isCommentRestricted(firstDetail.isCommentRestricted())
                            .build();
                })
                .collect(Collectors.toList());
        return issueResponses;
    }
    public static Page<IssueResponse> fromIssueDetailPage(Page<IssueStationDetail> issueStationDetails) {
        // 그루핑 `id`
        Map<Long, List<IssueStationDetail>> groupedById = issueStationDetails.stream()
                .collect(Collectors.groupingBy(
                        IssueStationDetail::getId,
                        LinkedHashMap::new, // 순서를 유지하기 위해 LinkedHashMap 사용
                        Collectors.toList()
                ));
        // Grouped 데이터를 IssueResponse로 변환
        List<IssueResponse> issueResponses = groupedById.values().stream()
                .map(details -> {
                    // 동일한 `id`의 IssueStationDetail 데이터를 처리
                    IssueStationDetail firstDetail = details.get(0); // 대표 데이터

                    // StationDto 리스트 생성
                    List<StationDto> stationDtoList = details.stream()
                            .map(detail -> StationDto.builder()
                                    .line(detail.getLine())
                                    .stationName(detail.getStationName())
                                    .build())
                            .collect(Collectors.toList());

                    // Line 리스트 생성
                    List<String> lineList = details.stream()
                            .map(IssueStationDetail::getLine)
                            .distinct()
                            .collect(Collectors.toList());

                    // IssueResponse 생성
                    return IssueResponse.builder()
                            .id(firstDetail.getId())
                            .title(firstDetail.getTitle())
                            .content(firstDetail.getContent())
                            .keyword(firstDetail.getKeyword())
                            .agoTime(getTime(firstDetail.getStartDate()))
                            .stationDtos(stationDtoList)
                            .lines(lineList)
                            .likeCount(firstDetail.getLikeCount())
                            .commentCount(firstDetail.getCommentCount())
                            .startDate(firstDetail.getStartDate())
                            .expireDate(firstDetail.getExpireDate())
                            .isLike(firstDetail.isLike())
                            .isCommentRestricted(firstDetail.isCommentRestricted())
                            .build();
                })
                .collect(Collectors.toList());

        // Page 객체로 변환
        return new PageImpl<>(issueResponses, issueStationDetails.getPageable(), issueStationDetails.getTotalElements());
    }
}
