package com.gazi.gazi_renew.member.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class IsMemberResponse {
    private final Boolean isUser;
    @Builder
    public IsMemberResponse(Boolean isUser) {
        this.isUser = isUser;
    }
}
