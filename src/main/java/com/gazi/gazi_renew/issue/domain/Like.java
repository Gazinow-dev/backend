package com.gazi.gazi_renew.issue.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
public class Like {
    private final Long id;
    private final Issue issue;
    @Builder
    public Like(Long id, Issue issue) {
        this.id = id;
        this.issue = issue;
    }
}
