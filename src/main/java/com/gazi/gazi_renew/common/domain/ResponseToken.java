package com.gazi.gazi_renew.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ResponseToken {
    private final String grantType;
    private final Long memberId;
    private final String nickName;
    private final String email;
    private final String firebaseToken;
    private final String accessToken;
    private final String refreshToken;
    private final Long accessTokenExpirationTime;
    private final Long refreshTokenExpirationTime;
    private final boolean notificationByKeyword;
    private final boolean notificationByRepost;
    private final boolean notificationByLike;
    @Builder
    public ResponseToken(String grantType, Long memberId, String nickName, String email, String firebaseToken, String accessToken, String refreshToken, Long accessTokenExpirationTime, Long refreshTokenExpirationTime, boolean notificationByKeyword, boolean notificationByRepost, boolean notificationByLike) {
        this.grantType = grantType;
        this.memberId = memberId;
        this.nickName = nickName;
        this.email = email;
        this.firebaseToken = firebaseToken;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
        this.notificationByKeyword = notificationByKeyword;
        this.notificationByRepost = notificationByRepost;
        this.notificationByLike = notificationByLike;
    }

    public ResponseToken login(String email, String nickName) {
        return ResponseToken.builder()
                .grantType(this.grantType)
                .accessToken(this.accessToken)
                .refreshToken(this.refreshToken)
                .accessTokenExpirationTime(this.accessTokenExpirationTime)
                .refreshTokenExpirationTime(this.refreshTokenExpirationTime)
                .email(email)
                .nickName(nickName)
                .build();
    }
}
