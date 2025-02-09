package com.gazi.gazi_renew.notification.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FcmMessage {
    private boolean validateOnly;
    private Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private Notification notification;
        private String token;
        private Data data;
        private Android android;  // Android 설정 추가
        private Apns apns;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {
        private String title;
        private String body;
        private String priority;  // 우선순위 추가
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Android {
        private String priority;  // Android 우선순위
        private int notification_priority;  // 알림 우선순위
        private boolean notification_visibility;  // 잠금화면 표시 여부
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Data {
        private String path;
        private String notificationId;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Apns {
        private Payload payload;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Payload {
        private Aps aps;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Aps {
        @JsonProperty("content-available")
        private int contentAvailable;
    }

    public static FcmMessage createMessage(Long id, String firebaseToken, String title, String body, String pathJson) {
        return FcmMessage.builder()
                .message(Message.builder()
                        .token(firebaseToken)
                        .notification(Notification.builder()
                                .title(title)
                                .body(body)
                                .priority("high")  // 높은 우선순위 설정
                                .build()
                        )
                        .android(Android.builder()  // Android 특정 설정 추가
                                .priority("high")
                                .notification_priority(2)  // PRIORITY_MAX
                                .notification_visibility(true)  // 잠금화면에서도 표시
                                .build()
                        )
                        .data(Data.builder()
                                .path(pathJson)
                                .notificationId(id.toString())
                                .build())
                        .apns(Apns.builder()
                                .payload(Payload.builder()
                                        .aps(Aps.builder()
                                                .contentAvailable(1)
                                                .build())
                                        .build())
                                .build())
                        .build())
                .validateOnly(false)
                .build();
    }
}