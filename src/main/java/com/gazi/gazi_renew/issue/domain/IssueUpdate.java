package com.gazi.gazi_renew.issue.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class IssueUpdate {
    private final Long id;
    private final String content;
}
