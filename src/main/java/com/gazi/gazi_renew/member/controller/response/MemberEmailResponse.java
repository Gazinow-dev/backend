package com.gazi.gazi_renew.member.controller.response;

import lombok.Builder;
import lombok.Getter;
@Getter
public class MemberEmailResponse {
    private final String email;
    @Builder
    public MemberEmailResponse(String email) {
        this.email = email;
    }
}
