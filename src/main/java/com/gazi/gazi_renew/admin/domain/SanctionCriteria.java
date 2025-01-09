package com.gazi.gazi_renew.admin.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SanctionCriteria {
    ADVERTISEMENT("광고 및 홍보"),
    OTHER_VIOLATIONS("광고 및 홍보 외 위반"),
    FALSE_REPORT("허위 신고"),
    NONE("판단되지 않음");

    private final String description;


    public String getDescription() {
        return description;
    }
}
