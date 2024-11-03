package com.gazi.gazi_renew.issue.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class IssueDetail {
    private final Issue issue;
    private final boolean isLike;
}
