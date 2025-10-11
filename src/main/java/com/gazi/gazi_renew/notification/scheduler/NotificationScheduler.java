package com.gazi.gazi_renew.notification.scheduler;


import com.gazi.gazi_renew.notification.controller.port.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
@RequiredArgsConstructor
public class NotificationScheduler {
    private final FcmService fcmService;
    @Scheduled(cron = "0 0 21 * * *", zone = "Asia/Seoul")
    public void nextDayIssueNotify() throws IOException {
        fcmService.nextDayIssueSendMessageTo();
    }
}
