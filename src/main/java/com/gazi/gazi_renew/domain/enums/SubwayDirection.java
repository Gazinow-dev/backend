package com.gazi.gazi_renew.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public enum SubwayDirection {
    Clockwise("내선"),
    Counterclockwise("외선"),
    Sinjeong("신정지선"),
    Seongsu("성수지선");


    private final String text;
}
