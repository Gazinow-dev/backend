package com.gazi.gazi_renew.member.controller.response;

import com.gazi.gazi_renew.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberAlertAgreeResponse {
    private final String email;
    private final boolean alertAgree;
    @Builder
    public MemberAlertAgreeResponse(String email, boolean alertAgree) {
        this.email = email;
        this.alertAgree = alertAgree;
    }

    public static MemberAlertAgreeResponse pushAlertAgreeFrom(Member member) {
        return MemberAlertAgreeResponse.builder()
                .email(member.getEmail())
                .alertAgree(member.getPushNotificationEnabled())
                .build();
    }
    public static MemberAlertAgreeResponse mySavedRouteAlertAgreeFrom(Member member) {
        return MemberAlertAgreeResponse.builder()
                .email(member.getEmail())
                .alertAgree(member.getMySavedRouteNotificationEnabled())
                .build();
    }
    public static MemberAlertAgreeResponse routeDetailAlertAgreeFrom(Member member) {
        return MemberAlertAgreeResponse.builder()
                .email(member.getEmail())
                .alertAgree(member.getRouteDetailNotificationEnabled())
                .build();
    }
}
