package com.gazi.gazi_renew.notification.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UnreadNotificationCountResponse {
    private final int unreadNotificationCount;
    @Builder
    public UnreadNotificationCountResponse(int unreadNotificationCount) {
        this.unreadNotificationCount = unreadNotificationCount;
    }

    public static UnreadNotificationCountResponse from(Long unreadNotificationCount) {
        return UnreadNotificationCountResponse.builder()
                .unreadNotificationCount(unreadNotificationCount.intValue())
                .build();
    }
}
