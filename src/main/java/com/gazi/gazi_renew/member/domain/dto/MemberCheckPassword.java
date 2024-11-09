package com.gazi.gazi_renew.member.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberCheckPassword {
    private final String checkPassword;
    @Builder
    public MemberCheckPassword(String checkPassword) {
        this.checkPassword = checkPassword;
    }

    public static MemberCheckPassword fromMemberChangePassword(MemberChangePassword memberChangePassword) {
        return MemberCheckPassword.builder()
                .checkPassword(memberChangePassword.getCurPassword())
                .build();
    }
}