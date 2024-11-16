package com.gazi.gazi_renew.issue.domain;

import com.gazi.gazi_renew.issue.domain.dto.LikeCreate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Like {
    private final Long id;
    private final Issue issue;
    private final Long memberId;
    @Builder
    public Like(Long id, Issue issue, Long memberId) {
        this.id = id;
        this.issue = issue;
        this.memberId = memberId;
    }
    public static Like from(LikeCreate likeCreate, Long memberId, Issue issue) {
        return Like.builder()
                .issue(issue)
                .memberId(memberId)
                .build();
    }
}
