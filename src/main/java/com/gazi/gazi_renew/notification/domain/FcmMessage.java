package com.gazi.gazi_renew.notification.domain;

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
    }
    public static FcmMessage createMessage(String firebaseToken, String title, String body, String pathJson) {
        return FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(firebaseToken)
                        .notification(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .build()
                        )
                        .data(FcmMessage.Data.builder().path(pathJson)
                                .build()).build()).validateOnly(false).build();
    }
}
