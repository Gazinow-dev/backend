package com.gazi.gazi_renew.issue.infrastructure;

import com.gazi.gazi_renew.issue.domain.Like;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "like_table")
@Entity
public class LikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long issueId;
    private Long memberId;

    public static LikeEntity from(Like like) {
        LikeEntity likeEntity = new LikeEntity();
        likeEntity.id = like.getId();
        likeEntity.memberId = like.getMemberId();
        likeEntity.issueId = like.getIssueId();
        return likeEntity;
    }
    public Like toModel() {
        return Like.builder()
                .id(id)
                .issueId(issueId)
                .memberId(memberId)
                .build();
    }
}

