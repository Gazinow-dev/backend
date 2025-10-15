package com.gazi.gazi_renew.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FcmMessageTemplate {

    NEXT_DAY_ISSUE("내일 내가 가는 길에 예정된 이슈가 있어요. 확인하고 미리 준비해보세요!");
    private final String title;
}