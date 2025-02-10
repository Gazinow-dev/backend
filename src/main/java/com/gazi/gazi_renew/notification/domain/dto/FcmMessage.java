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
        private Android android;
        private Apns apns;
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
    public static class Android {
        private String priority;
        private AndroidNotification notification;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class AndroidNotification {
        private String visibility;  // PUBLIC, PRIVATE, SECRET
        private String channel_id;
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
                                .build()
                        )
                        .android(Android.builder()
                                .priority("high")
                                .notification(AndroidNotification.builder()
                                        .visibility("PUBLIC")
                                        .channel_id("high_priority_channel")
                                        .build())
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