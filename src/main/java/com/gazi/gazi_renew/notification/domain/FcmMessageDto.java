package com.gazi.gazi_renew.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FcmMessageDto {
    private boolean validateOnly;
    private FcmMessageDto.Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private FcmMessageDto.Notification notification;
        private String token;
        private FcmMessageDto.Data data;
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
    public static FcmMessageDto createMessage(String firebaseToken, String title, String body, String pathJson) {
        return FcmMessageDto.builder()
                .message(FcmMessageDto.Message.builder()
                        .token(firebaseToken)
                        .notification(FcmMessageDto.Notification.builder()
                                .title(title)
                                .body(body)
                                .build()
                        )
                        .data(FcmMessageDto.Data.builder().path(pathJson)
                                .build()).build()).validateOnly(false).build();
    }
}
