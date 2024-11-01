package com.gazi.gazi_renew.notification.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmDto {
    private String token;
    private String title;
    private String body;
    private String issueType;
    private Long myRoadId;
}