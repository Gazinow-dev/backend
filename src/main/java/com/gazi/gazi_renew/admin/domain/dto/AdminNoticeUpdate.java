package com.gazi.gazi_renew.admin.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AdminNoticeUpdate {
    private final Long noticeId;
    private final String noticeTitle;
    private final String noticeContent;
    @Builder
    public AdminNoticeUpdate(Long noticeId, String noticeTitle, String noticeContent) {
        this.noticeId = noticeId;
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
    }
}
