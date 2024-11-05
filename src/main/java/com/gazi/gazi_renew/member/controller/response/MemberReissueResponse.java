package com.gazi.gazi_renew.member.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberReissueResponse {
    private final String accessToken;
    private final String refreshToken;
    @Builder
    public MemberReissueResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
