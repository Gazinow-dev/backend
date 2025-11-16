package com.gazi.gazi_renew.admin.infrastructure.jpa;

import com.gazi.gazi_renew.admin.infrastructure.entity.AdminNoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface AdminNoticeJpaRepositoryImpl extends JpaRepository<AdminNoticeEntity, Long> {
    @Modifying
    @Query("UPDATE AdminNoticeEntity a SET a.noticeTitle = :noticeTitle , a.noticeContent = :noticeContent, a.modifiedAt = :modifiedAt" +
            "  WHERE a.id = :id")
    void updateNotification(Long id, String noticeTitle, String noticeContent, LocalDateTime modifiedAt);

}
