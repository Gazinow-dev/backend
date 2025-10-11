package com.gazi.gazi_renew.notification.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NextDayNotificationFcmMessage {

    private boolean validateOnly;
    private NextDayNotificationFcmMessage.Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private NextDayNotificationFcmMessage.Notification notification;
        private String token;
        private NextDayNotificationFcmMessage.Data data;
        private NextDayNotificationFcmMessage.Apns apns;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {
        private String title;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Data {
        private Integer nextDayIssueCount;
        private List<Long> myFindRoadIdList;
    }
    @Builder
    @AllArgsConstructor
    @Getter
    public static class Apns {
        private NextDayNotificationFcmMessage.Payload payload;
    }
    @Builder
    @AllArgsConstructor
    @Getter
    public static class Payload {
        private NextDayNotificationFcmMessage.Aps aps;
    }
    @Builder
    @AllArgsConstructor
    @Getter
    public static class Aps {
        @JsonProperty("content-available")
        private int contentAvailable;
    }

    public static NextDayNotificationFcmMessage createMessage(List<Long> myFindRoadIdList, String firebaseToken, String title, Integer nextDayIssueCount) {
        return NextDayNotificationFcmMessage.builder()
                .message(NextDayNotificationFcmMessage.Message.builder()
                        .token(firebaseToken)
                        .notification(NextDayNotificationFcmMessage.Notification.builder()
                                .title(title)
                                .build()
                        )
                        .data(Data.builder().nextDayIssueCount(nextDayIssueCount)
                                .myFindRoadIdList(myFindRoadIdList)
                                .build())
                        .apns(NextDayNotificationFcmMessage.Apns.builder()
                                .payload(NextDayNotificationFcmMessage.Payload.builder()
                                        .aps(NextDayNotificationFcmMessage.Aps.builder()
                                                .contentAvailable(1)  // 설정된 contentAvailable
                                                .build())
                                        .build())
                                .build())
                        .build())
                .validateOnly(false)
                .build();
    }
}
