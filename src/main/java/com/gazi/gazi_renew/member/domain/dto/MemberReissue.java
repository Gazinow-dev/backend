package com.gazi.gazi_renew.member.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberReissue {
    private final String accessToken;
    private final String refreshToken;
    private final String firebaseToken;
    @Builder
    public MemberReissue(String accessToken, String refreshToken, String firebaseToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.firebaseToken = firebaseToken;
    }
}
