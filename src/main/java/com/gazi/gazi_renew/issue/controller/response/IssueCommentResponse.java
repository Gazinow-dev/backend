package com.gazi.gazi_renew.issue.controller.response;

import com.gazi.gazi_renew.common.service.port.ClockHolder;
import com.gazi.gazi_renew.issue.domain.IssueComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.stream.Collectors;

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
    @Builder
    public IssueCommentResponse(Long issueCommentId, String issueCommentContent, String createdBy, String agoTime) {
        this.issueCommentId = issueCommentId;
        this.issueCommentContent = issueCommentContent;
        this.createdBy = createdBy;
        this.agoTime = agoTime;
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
                .build());
    }
}
