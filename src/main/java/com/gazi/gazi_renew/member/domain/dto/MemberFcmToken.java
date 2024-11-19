package com.gazi.gazi_renew.member.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberFcmToken {
    private final String email;
    private final String firebaseToken;
    @Builder
    public MemberFcmToken(String email, String firebaseToken) {
        this.email = email;
        this.firebaseToken = firebaseToken;
    }
}
