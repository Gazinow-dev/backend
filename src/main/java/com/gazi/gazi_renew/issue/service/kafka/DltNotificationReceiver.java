package com.gazi.gazi_renew.issue.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.notification.domain.dto.NotificationCreate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DltNotificationReceiver {

    private final RestTemplate restTemplate; // Slack 알림용
    private final ObjectMapper objectMapper; // JSON 인코딩용
    @Value("${slack.web-hook-url}")
    private String SLACK_WEBHOOK_URL;


    @KafkaListener(topics = "notification.dlt", groupId = "gazi", containerFactory = "notificationListenerContainerFactory") // DLT 토픽 리스너
    public void handleDltMessage(NotificationCreate notificationCreate) {
        try {
            log.error("DLT로 이동된 메시지: {}", notificationCreate);

            // Slack 알림 전송
            sendSlackNotification(notificationCreate);
        } catch (Exception e) {
            log.error("DLT 메시지 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private void sendSlackNotification(NotificationCreate notificationCreate) {
        try {
            // NotificationCreate 객체를 JSON 문자열로 변환
            String notificationJson = objectMapper.writeValueAsString(notificationCreate);

            // Slack 메시지 내용 생성
            String slackMessage = String.format(
                    "⚠️ DLT Message Detected ⚠️\n" +
                            "Details: ```%s```\n" +
                            "Please check the issue."
                    , notificationJson);

            // Slack Webhook으로 메시지 전송
            String payload = objectMapper.writeValueAsString(new SlackPayload(slackMessage));
            restTemplate.postForObject(SLACK_WEBHOOK_URL, payload, String.class);

            log.info("Slack 알림 전송 완료: {}", slackMessage);
        } catch (JsonProcessingException e) {
            log.error("Slack 메시지 생성 중 JSON 처리 오류: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Slack 알림 전송 실패: {}", e.getMessage(), e);
        }
    }

    // Slack Webhook Payload 클래스
    private static class SlackPayload {
        private final String text;

        public SlackPayload(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
