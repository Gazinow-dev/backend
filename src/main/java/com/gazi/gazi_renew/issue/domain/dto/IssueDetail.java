package com.gazi.gazi_renew.issue.domain.dto;

import com.gazi.gazi_renew.issue.domain.Issue;
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
