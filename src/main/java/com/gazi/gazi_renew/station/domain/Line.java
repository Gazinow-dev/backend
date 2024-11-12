package com.gazi.gazi_renew.station.domain;

import com.gazi.gazi_renew.issue.domain.Issue;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
public class Line {
    private final Long id;
    private final String lineName;
    private final List<Issue> issueList;
    @Builder
    public Line(Long id, String lineName, List<Issue> issueList) {
        this.id = id;
        this.lineName = lineName;
        this.issueList = issueList;
    }

    public Line addIssue(List<Issue> issueList) {
             return Line.builder()
                .id(this.id)
                .lineName(this.lineName)
                .issueList(issueList)
                .build();
    }
}
