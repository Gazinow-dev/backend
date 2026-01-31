package com.gazi.gazi_renew.notification.controller.response;


import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.notification.domain.NotificationHistory;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Getter
public class NotificationHistoryResponse {
    private final Long id;
    private final Long issueId;
    private final String notificationTitle;
    private final String notificationBody;
    private final boolean isRead;
    private final IssueKeyword keyword;
    private final String agoTime;
    @Builder
    public NotificationHistoryResponse(Long id, Long issueId, String notificationTitle, String notificationBody, boolean isRead, IssueKeyword keyword, String agoTime) {
        this.id = id;
        this.issueId = issueId;
        this.notificationTitle = notificationTitle;
        this.notificationBody = notificationBody;
        this.isRead = isRead;
        this.keyword = keyword;
        this.agoTime = agoTime;
    }

    public static NotificationHistoryResponse from(NotificationHistory notificationHistory) {
        return NotificationHistoryResponse.builder()
                .id(notificationHistory.getId())
                .issueId(notificationHistory.getTargetId())
                .notificationTitle(notificationHistory.getNotificationTitle())
                .notificationBody(notificationHistory.getNotificationBody())
                .isRead(notificationHistory.isRead())
                .keyword(notificationHistory.getIssueKeyword())
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
        LocalDateTime now = LocalDateTime.now();
        // 초 단위 차이 계산
        long diff = Duration.between(startTime, now).getSeconds();

        // 1. 0초 ~ 44초: "방금 전" (Day.js: a few seconds)
        if (diff < 45) {
            return "방금 전";
        }
        // 2. 45초 ~ 89초: "1분 전" (Day.js: a minute)
        else if (diff < 90) {
            return "1분 전";
        }
        // 3. 90초 ~ 44분: "X분 전" (Day.js: X minutes)
        else if (diff < 45 * 60) {
            return Math.round((double) diff / 60) + "분 전";
        }
        // 4. 45분 ~ 89분: "1시간 전" (Day.js: an hour)
        else if (diff < 90 * 60) {
            return "1시간 전";
        }
        // 5. 90분 ~ 21시간: "X시간 전" (Day.js: X hours)
        else if (diff < 22 * 60 * 60) {
            return Math.round((double) diff / (60 * 60)) + "시간 전";
        }
        // 6. 22시간 ~ 35시간: "1일 전" (Day.js: a day)
        else if (diff < 36 * 60 * 60) {
            return "1일 전";
        }
        // 7. 36시간 ~ 25일: "X일 전" (Day.js: X days)
        else if (diff < 26 * 24 * 60 * 60) {
            return Math.round((double) diff / (24 * 60 * 60)) + "일 전";
        }
        // 8. 26일 ~ 45일: "1달 전" (Day.js: a month)
        else if (diff < 46 * 24 * 60 * 60) {
            return "1달 전";
        }
        // 9. 46일 ~ 10달: "X달 전" (Day.js: X months) - 한 달을 30일로 계산
        else if (diff < 320 * 24 * 60 * 60) {
            return Math.round((double) diff / (30 * 24 * 60 * 60)) + "달 전";
        }
        // 10. 11달 ~ 17달: "1년 전" (Day.js: a year)
        else if (diff < 548 * 24 * 60 * 60) {
            return "1년 전";
        }
        // 11. 18달 이상: "X년 전" (Day.js: X years)
        else {
            return Math.round((double) diff / (365 * 24 * 60 * 60)) + "년 전";
        }
    }

}
