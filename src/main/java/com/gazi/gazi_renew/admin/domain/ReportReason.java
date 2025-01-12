package com.gazi.gazi_renew.admin.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportReason {
    INAPPROPRIATE_LANGUAGE("음란성 댓글, 비속어, 폭언, 비하 등 불쾌한 내용을 포함하고 있어요"),
    MISLEADING_INFORMATION("갈등을 조장하거나 허위 사실을 퍼뜨려 혼란을 줄 수 있어요"),
    INAPPROPRIATE_CONTENT("도배나 개인정보 노출 등 커뮤니티 목적에 맞지 않는 댓글이에요"),
    OTHER("기타");
    private final String description;
}
