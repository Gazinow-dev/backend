package com.gazi.gazi_renew.member.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberCreate {
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9.%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$" , message = "이메일 형식이 맞지 않습니다.")
    private final String email;
    @NotBlank
    // 최소 8자리 이상 숫자, 특수문자가 1개 이상 포함
    @Pattern(regexp = "^(?=.*?[0-9])(?=.*?[~#?!@$ %^&*-]).{8,}$", message = "최소 8자리 이상 숫자, 특수문자가 1개 이상 포함되어야 합니다.")
    private final String password;
    @NotBlank
    private final String nickName;
    private final String firebaseToken;
    @Builder
    public MemberCreate(String email, String password, String nickName, String firebaseToken) {
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.firebaseToken = firebaseToken;
    }
    public MemberLogin toMemberLogin() {
        return MemberLogin.builder()
                .email(email)
                .password(password)
                .firebaseToken(firebaseToken)
                .build();
    }
}
