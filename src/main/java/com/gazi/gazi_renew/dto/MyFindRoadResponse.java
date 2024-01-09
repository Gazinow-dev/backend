package com.gazi.gazi_renew.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MyFindRoadResponse {
    private String roadName;
    private List<subway> subways;
    private List<MyFindRoadResponse.issue> issues;

    @Getter
    @Setter
    @Builder
    public static class subway {
        private String subwayName;
        private String line;
    }

    // 이슈
    @Getter
    public class issue{
        //어디에서 어디사이에서 발생한 이슈인지
        private String point;
    }

}
