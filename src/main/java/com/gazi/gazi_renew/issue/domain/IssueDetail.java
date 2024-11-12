package com.gazi.gazi_renew.issue.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class IssueDetail {
    private final Issue issue;
    private final boolean isLike;
    @Builder
    public IssueDetail(Issue issue, boolean isLike) {
        this.issue = issue;
        this.isLike = isLike;
    }
}
