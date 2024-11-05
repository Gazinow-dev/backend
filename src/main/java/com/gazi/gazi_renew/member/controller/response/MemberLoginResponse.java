package com.gazi.gazi_renew.member.controller.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
public class MemberLoginResponse {
    private final String email;
    private final String password;
    @Builder
    public MemberLoginResponse(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
