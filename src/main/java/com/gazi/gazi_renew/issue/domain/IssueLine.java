package com.gazi.gazi_renew.issue.domain;

import com.gazi.gazi_renew.issue.domain.dto.IssueCreate;
import com.gazi.gazi_renew.station.domain.Line;
import lombok.Builder;
import lombok.Getter;


@Getter
public class IssueLine {
    private final Long id;
    private final Issue issue;
    private final Line line;
    @Builder
    public IssueLine(Long id, Issue issue, Line line) {
        this.id = id;
        this.issue = issue;
        this.line = line;
    }
    public static IssueLine from(Issue issue, Line line) {
        return IssueLine.builder()
                .issue(issue)
                .line(line)
                .build();
    }
}
