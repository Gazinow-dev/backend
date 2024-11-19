package com.gazi.gazi_renew.issue.domain;

import com.gazi.gazi_renew.issue.domain.dto.LikeCreate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Like {
    private final Long id;
    private final Long issueId;
    private final Long memberId;
    @Builder
    public Like(Long id, Long issueId, Long memberId) {
        this.id = id;
        this.issueId = issueId;
        this.memberId = memberId;
    }
    public static Like from(LikeCreate likeCreate, Long memberId, Issue issue) {
        return Like.builder()
                .issueId(issue.getId())
                .memberId(memberId)
                .build();
    }
}
