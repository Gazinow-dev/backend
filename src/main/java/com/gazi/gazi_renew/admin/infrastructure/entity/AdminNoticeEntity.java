package com.gazi.gazi_renew.admin.infrastructure.entity;

import com.gazi.gazi_renew.admin.domain.AdminNotice;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "admin_notice")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminNoticeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String noticeTitle;
    private String noticeContent;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static AdminNoticeEntity from(AdminNotice adminNotice) {
        AdminNoticeEntity adminNoticeEntity = new AdminNoticeEntity();
        adminNoticeEntity.id = adminNotice.getNoticeId();
        adminNoticeEntity.noticeTitle = adminNotice.getNoticeTitle();
        adminNoticeEntity.noticeContent = adminNotice.getNoticeContent();
        adminNoticeEntity.createdAt = adminNotice.getCreatedAt();
        adminNoticeEntity.noticeTitle = adminNotice.getNoticeTitle();
        adminNoticeEntity.createdAt = adminNotice.getCreatedAt();
        adminNoticeEntity.modifiedAt = adminNotice.getModifiedAt();

        return adminNoticeEntity;
    }

    public AdminNotice toModel() {
        return AdminNotice.builder()
                .noticeId(id)
                .noticeTitle(noticeTitle)
                .noticeContent(noticeContent)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .build();
    }
}
