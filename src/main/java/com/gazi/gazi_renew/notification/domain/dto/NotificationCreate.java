package com.gazi.gazi_renew.notification.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
public class NotificationCreate {
    private final Long myRoadId;
    private final Long issueId;
    private final Boolean sendNotification;

    @Builder
    @JsonCreator
    public NotificationCreate(
            @JsonProperty("myRoadId") Long myRoadId,
            @JsonProperty("issueId") Long issueId,
            @JsonProperty("sendNotification") Boolean sendNotification) {
        this.myRoadId = myRoadId;
        this.issueId = issueId;
        this.sendNotification = sendNotification;
    }
}