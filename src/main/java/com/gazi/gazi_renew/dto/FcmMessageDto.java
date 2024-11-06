package com.gazi.gazi_renew.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

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
        private boolean contentAvailable;
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
                .message(Message.builder()
                        .token(firebaseToken)
                        .notification(Notification.builder()
                                .title(title)
                                .body(body)
                                .build()
                        )
                        .data(Data.builder().path(pathJson)
                                .build())
                        .contentAvailable(true)
                        .build())
                .validateOnly(false).build();
    }
}
