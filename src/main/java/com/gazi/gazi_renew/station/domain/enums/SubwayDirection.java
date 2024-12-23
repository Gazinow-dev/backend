package com.gazi.gazi_renew.station.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubwayDirection {
    Clockwise("내선"),
    Counterclockwise("외선"),
    Sinjeong("신정지선"),
    Seongsu("성수지선");


    private final String text;
}
