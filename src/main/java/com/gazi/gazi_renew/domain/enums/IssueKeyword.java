package com.gazi.gazi_renew.domain.enums;

import lombok.Getter;

@Getter
public enum IssueKeyword {
    자연재해("자연재해"),
    연착("연착"),
    혼잡("혼잡"),
    행사("행사"),
    사고("사고"),
    공사("공사"),
    시위("시위");

    private final String text;

    IssueKeyword(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
