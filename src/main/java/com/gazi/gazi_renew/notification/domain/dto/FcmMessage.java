package com.gazi.gazi_renew.notification.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FcmMessage {
    private boolean validateOnly;
    private FcmMessage.Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private FcmMessage.Notification notification;
        private String token;
        private FcmMessage.Data data;
        private FcmMessage.Apns apns;
        private FcmMessage.Android android;  // ✅ Android 추가
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {
        private String title;
        private String body;
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

    // ✅ Android 관련 설정 (올바른 구조)
    @Builder
    @AllArgsConstructor
    @Getter
    public static class Android {
        private String priority;
        private AndroidNotification notification;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class AndroidNotification {
        private String channelId;
        private String title;  // ✅ title 추가
        private String body;   // ✅ body 추가
    }


    public static FcmMessage createMessage(Long id, String firebaseToken, String title, String body, String pathJson) {
        return FcmMessage.builder()
                .message(Message.builder()
                        .token(firebaseToken)
                        .notification(Notification.builder()
                                .title(title)
                                .body(body)
                                .build()
                        )
                        .data(Data.builder()
                                .path(pathJson)
                                .notificationId(id.toString())
                                .build()
                        )
                        .apns(Apns.builder()
                                .payload(Payload.builder()
                                        .aps(Aps.builder()
                                                .contentAvailable(1)  // iOS 백그라운드 푸시 활성화
                                                .build())
                                        .build())
                                .build())
                        .android(Android.builder()
                                .priority("high")
                                .notification(AndroidNotification.builder()
                                        .channelId("high_priority_channel")
                                        .title(title)  // ✅ 추가
                                        .body(body)    // ✅ 추가
                                        .build())
                                .build())
                        .build())
                .validateOnly(false)
                .build();
    }
}
