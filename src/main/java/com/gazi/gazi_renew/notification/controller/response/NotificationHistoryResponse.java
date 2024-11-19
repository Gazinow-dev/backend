package com.gazi.gazi_renew.notification.controller.response;


import com.gazi.gazi_renew.notification.domain.NotificationHistory;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class NotificationHistoryResponse {
    private final Long id;
    private final Long issueId;
    private final String notificationTitle;
    private final String notificationBody;
    private final boolean isRead;
    private final String agoTime;
    @Builder
    public NotificationHistoryResponse(Long id, Long issueId, String notificationTitle, String notificationBody, boolean isRead, String agoTime) {
        this.id = id;
        this.issueId = issueId;
        this.notificationTitle = notificationTitle;
        this.notificationBody = notificationBody;
        this.isRead = isRead;
        this.agoTime = agoTime;
    }

    public static NotificationHistoryResponse from(NotificationHistory notificationHistory) {
        return NotificationHistoryResponse.builder()
                .id(notificationHistory.getId())
                .issueId(notificationHistory.getIssueId())
                .notificationTitle(notificationHistory.getNotificationTitle())
                .notificationBody(notificationHistory.getNotificationBody())
                .isRead(notificationHistory.isRead())
                .agoTime(getTime(notificationHistory.getStartDate()))
                .build();
    }
    public static Page<NotificationHistoryResponse> fromPage(Page<NotificationHistory> notificationHistories) {
        Page<NotificationHistoryResponse> notificationHistoryResponses = new PageImpl<>(
                notificationHistories.stream().map(NotificationHistoryResponse::from).collect(Collectors.toList()),
                notificationHistories.getPageable(),
                notificationHistories.getTotalElements()
        );
        return notificationHistoryResponses;
    }
    // 시간 구하기 로직
    private static String getTime(LocalDateTime startTime) {
        System.out.println(startTime);

        LocalDateTime nowDate = LocalDateTime.now();
        Duration duration = Duration.between(startTime, nowDate);
        Long time = duration.getSeconds();
        String formatTime;

        if (time > 60 && time <= 3600) {
            // 분
            time = time / 60;
            formatTime = time + "분 전";
        } else if (time > 3600 && time <= 86400) {
            time = time / (60 * 60);
            formatTime = time + "시간 전";
        } else if (time > 86400) {
            time = time / 86400;
            formatTime = time + "일 전";
        } else {
            formatTime = time + "초 전";
        }

        return formatTime;
    }

}
