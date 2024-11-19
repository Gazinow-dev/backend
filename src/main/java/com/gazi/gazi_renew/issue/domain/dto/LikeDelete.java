package com.gazi.gazi_renew.issue.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gazi.gazi_renew.issue.domain.Issue;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LikeDelete {
    private final Long issueId;
    @Builder
    @JsonCreator
    public LikeDelete(Long issueId) {
        this.issueId = issueId;
    }
}
