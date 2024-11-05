package com.gazi.gazi_renew.member.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberAlertAgreeResponse {
    private final String email;
    private final boolean alertAgree;
    @Builder
    public MemberAlertAgreeResponse(String email, boolean alertAgree) {
        this.email = email;
        this.alertAgree = alertAgree;
    }
}
