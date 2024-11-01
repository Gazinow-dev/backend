package com.gazi.gazi_renew.station.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class SubwayDataResponse {
    private Double lat;
    private Double lng;

    @Setter
    @Builder
    @Getter
    public static class SubwayInfo{
        private String name;
        private String line;
    }
}