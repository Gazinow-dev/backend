package com.gazi.gazi_renew.dto;

import lombok.*;
import org.json.JSONObject;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmSendDto {
    private String token;
    private String title;
    private String body;
    private String issueType;
    private Long myRoadId;
}
