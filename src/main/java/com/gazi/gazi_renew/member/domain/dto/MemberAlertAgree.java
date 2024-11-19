package com.gazi.gazi_renew.member.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberAlertAgree {
    private final String email;
    private final boolean alertAgree;
    @Builder
    public MemberAlertAgree(String email, boolean alertAgree) {
        this.email = email;
        this.alertAgree = alertAgree;
    }
}
