package com.gazi.gazi_renew.issue.domain;

import com.gazi.gazi_renew.common.service.port.ClockHolder;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueCommentUpdate;
import com.gazi.gazi_renew.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter
public class IssueComment {
    private final Long issueCommentId;
    private final Long issueId;
    private final Long memberId;
    private final String issueCommentContent;
    private final String createdBy;
    private final LocalDateTime createdAt;

    @Builder
    public IssueComment(Long issueCommentId, Long issueId, Long memberId, String issueCommentContent, String createdBy, LocalDateTime createdAt) {
        this.issueCommentId = issueCommentId;
        this.issueId = issueId;
        this.memberId = memberId;
        this.issueCommentContent = issueCommentContent;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }
    public IssueComment update(IssueCommentUpdate issueCommentUpdate, ClockHolder clockHolder) {
        return IssueComment.builder()
                .issueCommentId(this.issueCommentId)
                .issueId(this.issueId)
                .memberId(this.memberId)
                .issueCommentContent(issueCommentUpdate.getIssueCommentContent())
                .createdBy(this.createdBy)
                .createdAt(clockHolder.now())
                .build();
    }

    public static IssueComment from(IssueCommentCreate issueCommentCreate, Member member, ClockHolder clockHolder) {
        return IssueComment.builder()
                .issueId(issueCommentCreate.getIssueId())
                .memberId(member.getId())
                .issueCommentContent(issueCommentCreate.getIssueCommentContent())
                .createdBy(member.getNickName())
                .createdAt(clockHolder.now())
                .build();
    }
    // 시간 구하기 로직
    public String getTime(LocalDateTime createdAt) {
        LocalDateTime nowDate = LocalDateTime.now();
        Duration duration = Duration.between(createdAt, nowDate);
        Long time = duration.getSeconds();
        String formatTime;
        if (time <= 60) {
            formatTime = "지금";
        } else if (time > 60 && time < 3600) {
            // 분
            time = time / 60;
            formatTime = time + "분 전";
        } else if (time > 3600 && time < 86400) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a hh:mm")
                    .withLocale(Locale.forLanguageTag("ko"));
            formatTime = createdAt.format(formatter);
        } else {
            time = time / 86400;
            formatTime = time + "일 전";
        }

        return formatTime;
    }
}
