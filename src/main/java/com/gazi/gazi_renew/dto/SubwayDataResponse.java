package com.gazi.gazi_renew.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SubwayDataResponse {
    private Double lat;
    private Double lng;
}