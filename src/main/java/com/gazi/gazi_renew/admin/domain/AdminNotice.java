package com.gazi.gazi_renew.admin.domain;

import com.gazi.gazi_renew.admin.domain.dto.AdminNoticeCreate;
import com.gazi.gazi_renew.common.service.port.ClockHolder;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AdminNotice {
    private final Long noticeId;
    private final String noticeTitle;
    private final String noticeContent;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    @Builder
    public AdminNotice(Long noticeId, String noticeTitle, String noticeContent, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.noticeId = noticeId;
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static AdminNotice from(AdminNoticeCreate adminNoticeCreate, ClockHolder clockHolder) {
        return AdminNotice.builder()
                .noticeTitle(adminNoticeCreate.getNoticeTitle())
                .noticeContent(adminNoticeCreate.getNoticeContent())
                .createdAt(clockHolder.now())
                .modifiedAt(clockHolder.now())
                .build();
    }


    public AdminNotice update(AdminNoticeCreate adminNoticeCreate, ClockHolder clockHolder) {
        return AdminNotice.builder()
                .noticeId(this.noticeId)
                .noticeTitle(adminNoticeCreate.getNoticeTitle())
                .noticeContent(adminNoticeCreate.getNoticeContent())
                .createdAt(clockHolder.now())
                .modifiedAt(clockHolder.now())
                .build();
    }
}
