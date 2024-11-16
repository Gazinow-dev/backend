package com.gazi.gazi_renew.issue.infrastructure;

import com.gazi.gazi_renew.issue.domain.Like;
import com.gazi.gazi_renew.member.infrastructure.MemberEntity;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id")
    private IssueEntity issueEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    public static LikeEntity from(Like like) {
        LikeEntity likeEntity = new LikeEntity();
        likeEntity.id = like.getId();
        likeEntity.issueEntity = IssueEntity.from(like.getIssue());
        return likeEntity;
    }
    public Like toModel() {
        return Like.builder()
                .id(id)
                .issue(issueEntity.toModel())
                .memberId(memberEntity.getId())
                .build();
    }
}

