package com.gazi.gazi_renew.notification.domain;

import com.gazi.gazi_renew.common.service.port.ClockHolder;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationHistory {
    private final Long id;
    private final Long memberId;
    private final Long issueId;
    private final String notificationTitle;
    private final String notificationBody;
    private final boolean isRead; //true=읽음
    private final IssueKeyword issueKeyword;
    private final LocalDateTime startDate;
    @Builder
    public NotificationHistory(Long id, Long memberId, Long issueId, String notificationTitle, String notificationBody, boolean isRead, IssueKeyword issueKeyword, LocalDateTime startDate) {
        this.id = id;
        this.memberId = memberId;
        this.issueId = issueId;
        this.notificationTitle = notificationTitle;
        this.notificationBody = notificationBody;
        this.isRead = isRead;
        this.issueKeyword = issueKeyword;
        this.startDate = startDate;
    }

    public static NotificationHistory saveHistory(Long memberId, Long issueId, String notificationTitle, String notificationBody, IssueKeyword issueKeyword, LocalDateTime startDate) {
        return NotificationHistory.builder()
                .memberId(memberId)
                .issueId(issueId)
                .notificationTitle(notificationTitle)
                .notificationBody(notificationBody)
                .issueKeyword(issueKeyword)
                .isRead(false)
                .startDate(startDate)
                .build();
    }
}
