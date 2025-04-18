package com.gazi.gazi_renew.issue.domain.dto;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Getter
public class IssueStationDetail {
    private final Long id;
    private final String title;
    private final String content;
    private final int likeCount;
    private final int commentCount;
    private final boolean isLike;
    private final boolean isCommentRestricted;
    private final IssueKeyword keyword;
    private final LocalDateTime startDate;
    private final LocalDateTime expireDate;
    private final String line;
    private final String stationName;
    private final Integer issueStationCode;

    @Builder
    @QueryProjection
    public IssueStationDetail(Long id, String title, String content, int likeCount, int commentCount, boolean isLike, boolean isCommentRestricted, IssueKeyword keyword, LocalDateTime startDate, LocalDateTime expireDate, String line, String stationName, Integer issueStationCode) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.isLike = isLike;
        this.isCommentRestricted = isCommentRestricted;
        this.keyword = keyword;
        this.startDate = startDate;
        this.expireDate = expireDate;
        this.line = line;
        this.stationName = stationName;
        this.issueStationCode = issueStationCode;
    }
    public IssueStationDetail restrictedWriteComment(boolean memberRestricted) {
        return IssueStationDetail.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .likeCount(this.likeCount)
                .commentCount(this.commentCount)
                .isLike(this.isLike)
                .isCommentRestricted(memberRestricted)
                .keyword(this.keyword)
                .startDate(this.startDate)
                .expireDate(this.expireDate)
                .line(this.line)
                .stationName(this.stationName)
                .issueStationCode(this.issueStationCode)
                .build();
    }

    public IssueStationDetail fromLike(boolean isLike) {
        return IssueStationDetail.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .likeCount(this.likeCount)
                .commentCount(this.commentCount)
                .isLike(isLike)
                .keyword(this.keyword)
                .startDate(this.startDate)
                .expireDate(this.expireDate)
                .line(this.line)
                .stationName(this.stationName)
                .issueStationCode(this.issueStationCode)
                .build();
    }
    public static IssueStationDetail fromIssue(Issue issue) {
        return IssueStationDetail.builder()
                .id(issue.getId())
                .title(issue.getTitle())
                .content(issue.getContent())
                .likeCount(issue.getLikeCount())
                .keyword(issue.getKeyword())
                .startDate(issue.getStartDate())
                .expireDate(issue.getExpireDate())
                .build();
    }
    public static List<IssueStationDetail> applyTop5Policy(List<IssueStationDetail> topIssues, List<IssueStationDetail> activeOrTodayIssues) {
        Map<Long, List<IssueStationDetail>> filteredMap = new LinkedHashMap<>();

        for (IssueStationDetail topIssue : topIssues) {
            filteredMap
                    .computeIfAbsent(topIssue.getId(), k -> new ArrayList<>())
                    .add(topIssue);
        }

        Map<Long, List<IssueStationDetail>> grouped = activeOrTodayIssues.stream()
                .filter(candidate -> !filteredMap.containsKey(candidate.getId()))
                .collect(Collectors.groupingBy(IssueStationDetail::getId, LinkedHashMap::new, Collectors.toList()));

        Map<Long, List<IssueStationDetail>> missingMap = grouped.entrySet().stream()
                .limit(2)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        if (!missingMap.isEmpty()) {
            List<Long> replaceTargetIds = filteredMap.keySet().stream()
                    .skip(3)
                    .limit(missingMap.size())
                    .toList();

            for (Long replaceId : replaceTargetIds) {
                filteredMap.remove(replaceId);
            }

            filteredMap.putAll(missingMap);
        }

        return filteredMap.values().stream()
                .flatMap(Collection::stream)
                .toList();
    }


}
