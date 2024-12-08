package com.gazi.gazi_renew.issue.service.kafka;

import com.gazi.gazi_renew.notification.controller.port.FcmService;
import com.gazi.gazi_renew.notification.controller.port.NotificationService;
import com.gazi.gazi_renew.notification.domain.dto.NotificationCreate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationReceiver {

    private final FcmService fcmService;

    @KafkaListener(topics = "notification", groupId = "gazi", containerFactory = "notificationListenerContainerFactory")
    public void listener(NotificationCreate notificationCreate) throws IOException {
        log.info("kafka consumer 데이터 받음");
        fcmService.sendMessageTo(notificationCreate);
    }
}
