package com.gazi.gazi_renew.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
        private FcmMessageDto.Apns apns;
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
    public static FcmMessageDto createMessage(String firebaseToken, String title, String body, String pathJson) {
        return FcmMessageDto.builder()
                .message(Message.builder()
                        .token(firebaseToken)
                        .notification(Notification.builder()
                                .title(title)
                                .body(body)
                                .build()
                        )
                        .data(Data.builder().path(pathJson).build())
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
