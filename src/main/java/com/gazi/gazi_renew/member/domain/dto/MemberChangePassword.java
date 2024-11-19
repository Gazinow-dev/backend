package com.gazi.gazi_renew.member.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberChangePassword {
    @NotBlank
    private final String curPassword;
    @NotBlank
    private final String changePassword;
    @NotBlank
    private final String confirmPassword;
    @Builder
    public MemberChangePassword(String curPassword, String changePassword, String confirmPassword) {
        this.curPassword = curPassword;
        this.changePassword = changePassword;
        this.confirmPassword = confirmPassword;
    }
}
