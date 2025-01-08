package com.gazi.gazi_renew.issue.infrastructure.entity;

import com.gazi.gazi_renew.issue.domain.CommentLikes;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Table(name = "comment_likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentLikesEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private Long memberId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_comment_id", nullable = false)
    private IssueCommentEntity issueCommentEntity;

    public static CommentLikesEntity from(CommentLikes commentLikes) {
        CommentLikesEntity commentLikesEntity = new CommentLikesEntity();
        commentLikesEntity.id = commentLikes.getCommentLikesId();
        commentLikesEntity.memberId= commentLikes.getMemberId();
        commentLikesEntity.issueCommentEntity = IssueCommentEntity.from(commentLikes.getIssueComment());

        return commentLikesEntity;
    }
    public CommentLikes toModel() {
        return CommentLikes.builder()
                .commentLikesId(id)
                .issueComment(issueCommentEntity.toModel())
                .memberId(memberId)
                .build();
    }
}
