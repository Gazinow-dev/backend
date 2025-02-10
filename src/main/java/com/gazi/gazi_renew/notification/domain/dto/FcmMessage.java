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
    public static FcmMessage createMessage(Long id, String firebaseToken, String title, String body, String pathJson) {
        return FcmMessage.builder()
                .message(Message.builder()
                        .token(firebaseToken)
                        .notification(Notification.builder()
                                .title(title)
                                .body(body)
                                .build()
                        )
                        .data(Data.builder().path(pathJson)
                                .notificationId(id.toString()).build())
                        .apns(Apns.builder()
                                .payload(Payload.builder()
                                        .aps(Aps.builder()
                                                .contentAvailable(1)  // 설정된 contentAvailable
                                                .build())
                                        .build())
                                .build())
                        .build())
                .validateOnly(false)
                .build();
    }

}