package com.gazi.gazi_renew.issue.controller.response;

import com.gazi.gazi_renew.common.service.port.ClockHolder;
import com.gazi.gazi_renew.issue.domain.IssueComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;


@Getter
public class IssueCommentResponse {
    @Schema(description = "댓글 ID", example = "1")
    private final Long issueCommentId;
    @Schema(description = "댓글 내용", example = "댓글 테스트")
    private final String issueCommentContent;
    @Schema(description = "작성자", example = "가는길 지금")
    private final String createdBy;
    @Schema(description = "등록시간", example = "2분 전, 지금, 1일 전")
    private final String agoTime;
    @Schema(description = "내가 작성한 댓글 상태 값", example = "true, false")
    private final boolean isMine;
    @Schema(description = "댓글 좋아요 상태 값", example = "true, false")
    private final boolean isLiked;
    @Schema(description = "댓글 좋아요 갯수", example = "1")
    private final int likesCount;
    @Builder
    public IssueCommentResponse(Long issueCommentId, String issueCommentContent, String createdBy, String agoTime, boolean isMine, boolean isLiked, int likesCount) {
        this.issueCommentId = issueCommentId;
        this.issueCommentContent = issueCommentContent;
        this.createdBy = createdBy;
        this.agoTime = agoTime;
        this.isMine = isMine;
        this.isLiked = isLiked;
        this.likesCount = likesCount;
    }

    public static IssueCommentResponse from(IssueComment issueComment, ClockHolder clockHolder) {
        return IssueCommentResponse.builder()
                .issueCommentId(issueComment.getIssueCommentId())
                .issueCommentContent(issueComment.getIssueCommentContent())
                .createdBy(issueComment.getCreatedBy())
                .agoTime(issueComment.formatTime(clockHolder))
                .build();
    }
    public static Page<IssueCommentResponse> fromPage(Page<IssueComment> issueCommentList, ClockHolder clockHolder) {
        return issueCommentList.map(issueComment -> IssueCommentResponse.builder()
                .issueCommentId(issueComment.getIssueCommentId())
                .issueCommentContent(issueComment.getIssueCommentContent())
                .createdBy(issueComment.getCreatedBy())
                .agoTime(issueComment.formatTime(clockHolder))
                .isMine(issueComment.isMine())
                .isLiked(issueComment.isLiked())
                .likesCount(issueComment.getLikesCount())
                .build());
    }
}
