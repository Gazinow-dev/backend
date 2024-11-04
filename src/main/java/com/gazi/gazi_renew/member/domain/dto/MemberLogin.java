package com.gazi.gazi_renew.member.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
public class MemberLogin {
    @NotBlank(message = "Email는 필수 입력 값입니다.")
    private final String email;

    @NotBlank(message = "Password는 필수 입력 값입니다.")
    private final String password;

    private final String firebaseToken;
    @Builder
    public MemberLogin(String email, String password, String firebaseToken) {
        this.email = email;
        this.password = password;
        this.firebaseToken = firebaseToken;
    }

    public UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }

}
