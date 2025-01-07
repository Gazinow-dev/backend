package com.gazi.gazi_renew.admin.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;



@Getter
public class ReportCreate {
    @Schema(description = "신고된 댓글 ID", example = "1")
    private final Long reportedCommentId;
    @Schema(description = "신고 사유 선택형", example = "INAPPROPRIATE_LANGUAGE, MISLEADING_INFORMATION, INAPPROPRIATE_CONTENT, OTHER")
    private final String reason;
    @Size(max = 100, message = "신고 사유는 100자를 넘을 수 없습니다.")
    @Schema(description = "신고 사유 설명", example = "댓글이 ~~")
    private final String reasonDescription;
    @Builder
    public ReportCreate(Long reportedCommentId, String reason, String reasonDescription) {
        this.reportedCommentId = reportedCommentId;
        this.reason = reason;
        this.reasonDescription = reasonDescription;
    }
}
