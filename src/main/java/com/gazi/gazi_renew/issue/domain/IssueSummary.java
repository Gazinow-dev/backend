package com.gazi.gazi_renew.issue.domain;

import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import lombok.Builder;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class IssueSummary {
    private final Long id;
    private final String title;
    private final int likeCount;
    private final IssueKeyword keyword;
    private final String agoTime; // 몇분전, 몇시간전...
    @Builder
    public IssueSummary(Long id, String title, int likeCount, IssueKeyword keyword, String agoTime) {
        this.id = id;
        this.title = title;
        this.likeCount = likeCount;
        this.keyword = keyword;
        this.agoTime = agoTime;
    }

    public static List<IssueSummary> getIssueSummaryDto(List<Issue> issueList){
        if (issueList == null) {
            return Collections.emptyList(); // issueList가 null일 경우 빈 리스트 반환
        }
        List<IssueSummary> issueSummaryDto = issueList.stream().map(
                m ->{
                    return IssueSummary.builder()
                            .id(m.getId())
                            .title(m.getTitle())
                            .likeCount(m.getLikeCount())
                            .keyword(m.getKeyword())
                            .build();
                }
        ).collect(Collectors.toList());
        return issueSummaryDto;
    }
    public static List<IssueSummary> getIssueSummaryDtoByLine(List<IssueSummary> issues){
        List<IssueSummary> issueSummaryDtoList = new ArrayList<>();

        Set<Long> idSet = new HashSet<>();
        // 중복된거면 넣지않기
        for(IssueSummary issue : issues){
            if (!idSet.contains(issue.getId())) {
                // HashSet에 아직 존재하지 않는 id인 경우에만 리스트에 추가합니다.
                issueSummaryDtoList.add(issue);
                idSet.add(issue.getId());
            }
        }

        return issueSummaryDtoList;
    }
}