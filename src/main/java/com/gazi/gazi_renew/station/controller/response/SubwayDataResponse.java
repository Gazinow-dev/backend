package com.gazi.gazi_renew.station.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class SubwayDataResponse {
    private final Double lat;
    private final Double lng;
    @Builder
    public SubwayDataResponse(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }
}