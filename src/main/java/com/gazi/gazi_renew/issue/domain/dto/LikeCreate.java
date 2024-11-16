package com.gazi.gazi_renew.issue.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LikeCreate {
    private final Long issueId;
    @Builder
    @JsonCreator
    public LikeCreate(Long issueId) {
        this.issueId = issueId;
    }
}
