package com.gazi.gazi_renew.member.controller.response;

import com.gazi.gazi_renew.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberNicknameResponse {
    private final String nickName;
    @Builder
    public MemberNicknameResponse(String nickName) {
        this.nickName = nickName;
    }

    public static MemberNicknameResponse from(Member member) {
        return MemberNicknameResponse.builder()
                .nickName(member.getNickName())
                .build();
    }
}
