package com.gazi.gazi_renew.member.controller.response;

import com.gazi.gazi_renew.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FcmTokenResponse {
    private final Long memberId;
    @Builder
    public FcmTokenResponse(Long memberId) {this.memberId = memberId;}

    public static FcmTokenResponse from(Member member) {
        return FcmTokenResponse.builder()
                .memberId(member.getId())
                .build();
    }
}
