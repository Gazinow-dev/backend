package com.gazi.gazi_renew.issue.domain.enums;

import java.time.DayOfWeek;

public enum


KoreanDayOfWeek {
    MONDAY("월"),
    TUESDAY("화"),
    WEDNESDAY("수"),
    THURSDAY("목"),
    FRIDAY("금"),
    SATURDAY("토"),
    SUNDAY("일");

    private final String koreanName;

    KoreanDayOfWeek(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public static String toKorean(DayOfWeek dayOfWeek) {
        return KoreanDayOfWeek.valueOf(dayOfWeek.name()).getKoreanName();
    }
}
