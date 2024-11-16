package com.gazi.gazi_renew.notification.domain.dto;

import lombok.*;

@Getter
public class NotificationCreate {
    private final Long myRoadId;
    private final Long issueId;
    @Builder
    public NotificationCreate(Long myRoadId, Long issueId) {
        this.myRoadId = myRoadId;
        this.issueId = issueId;
    }
}