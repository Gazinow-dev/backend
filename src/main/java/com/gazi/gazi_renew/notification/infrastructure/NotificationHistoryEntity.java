package com.gazi.gazi_renew.notification.infrastructure;

import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.notification.domain.NotificationHistory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notification_hitstory")
public class NotificationHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long memberId;
    private Long issueId;
    private String notificationTitle;
    private String notificationBody;
    private boolean isRead;
    private IssueKeyword issueKeyword;
    private LocalDateTime startDate;

    public NotificationHistory toModel() {
        return NotificationHistory.builder()
                .id(id)
                .memberId(memberId)
                .issueId(issueId)
                .notificationTitle(notificationTitle)
                .notificationBody(notificationBody)
                .isRead(isRead)
                .issueKeyword(issueKeyword)
                .startDate(startDate)
                .build();
    }

    public static NotificationHistoryEntity from(NotificationHistory notificationHistory) {
        NotificationHistoryEntity notificationHistoryEntity = new NotificationHistoryEntity();
        notificationHistoryEntity.memberId = notificationHistory.getMemberId();
        notificationHistoryEntity.issueId = notificationHistory.getIssueId();
        notificationHistoryEntity.notificationTitle = notificationHistory.getNotificationTitle();
        notificationHistoryEntity.notificationBody = notificationHistory.getNotificationBody();
        notificationHistoryEntity.isRead = notificationHistory.isRead();
        notificationHistoryEntity.issueKeyword = notificationHistory.getIssueKeyword();
        notificationHistoryEntity.startDate = notificationHistory.getStartDate();
        return notificationHistoryEntity;
    }
}
