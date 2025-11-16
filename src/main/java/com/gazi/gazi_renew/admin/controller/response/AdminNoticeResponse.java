package com.gazi.gazi_renew.admin.controller.response;

import com.gazi.gazi_renew.admin.domain.AdminNotice;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class AdminNoticeResponse {
    private final Long noticeId;
    private final String noticeTitle;
    private final String noticeContent;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    @Builder
    public AdminNoticeResponse(Long noticeId, String noticeTitle, String noticeContent, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.noticeId = noticeId;
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static List<AdminNoticeResponse> fromList(List<AdminNotice> adminNoticeList) {
        return adminNoticeList.stream().map(adminNotice -> {
            return AdminNoticeResponse.builder()
                    .noticeId(adminNotice.getNoticeId())
                    .noticeTitle(adminNotice.getNoticeTitle())
                    .noticeContent(adminNotice.getNoticeContent())
                    .createdAt(adminNotice.getCreatedAt())
                    .modifiedAt(adminNotice.getModifiedAt())
                    .build();
                }).collect(Collectors.toList());
            }

    public static AdminNoticeResponse from(AdminNotice adminNotice) {
        return AdminNoticeResponse.builder()
                .noticeId(adminNotice.getNoticeId())
                .noticeTitle(adminNotice.getNoticeTitle())
                .noticeContent(adminNotice.getNoticeContent())
                .createdAt(adminNotice.getCreatedAt())
                .modifiedAt(adminNotice.getModifiedAt())
                .build();
    }
}
