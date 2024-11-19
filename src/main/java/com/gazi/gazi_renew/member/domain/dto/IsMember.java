package com.gazi.gazi_renew.member.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class IsMember {
    private final String email;
    private final String nickname;
    @Builder
    public IsMember(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}
