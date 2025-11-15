package com.gazi.gazi_renew.admin.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AdminNoticeCreate {
    private final String noticeTitle;
    private final String noticeContent;
    private final LocalDateTime createdAt;
    @Builder
    public AdminNoticeCreate(String noticeTitle, String noticeContent, LocalDateTime createdAt) {
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
        this.createdAt = createdAt;
    }
}
