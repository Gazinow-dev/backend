package com.gazi.gazi_renew.notification.scheduler;


import com.gazi.gazi_renew.notification.controller.port.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class NotificationScheduler {
    private final NotificationService notificationService;
    @Scheduled(cron = "0 0 21 * * *", zone = "Asia/Seoul")
    public void nextDayIssueNotify() {
        notificationService.nextDayIssueNotify();
    }
}
