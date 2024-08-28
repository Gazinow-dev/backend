package com.gazi.gazi_renew.dto;

import lombok.*;
import org.json.JSONObject;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmSendDto {
    private Long myRoadId;
    private Long issueId;
}