package com.gazi.gazi_renew.issue.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class IssueUpdate {
    private final Long id;
    private final String content;
    @Builder
    public IssueUpdate(Long id, String content) {
        this.id = id;
        this.content = content;
    }
}
