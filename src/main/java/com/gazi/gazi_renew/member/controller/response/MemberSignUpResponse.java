package com.gazi.gazi_renew.member.controller.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberSignUpResponse {
    private final String email;
    private final String nickName;
    @Builder
    public MemberSignUpResponse(String email, String nickName) {
        this.email = email;
        this.nickName = nickName;
    }
}
