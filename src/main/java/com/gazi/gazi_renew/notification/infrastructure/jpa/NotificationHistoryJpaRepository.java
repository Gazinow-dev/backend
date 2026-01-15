package com.gazi.gazi_renew.notification.infrastructure.jpa;

import com.gazi.gazi_renew.notification.infrastructure.NotificationHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationHistoryJpaRepository extends JpaRepository<NotificationHistoryEntity, Long> {
    List<NotificationHistoryEntity> findAllByMemberId(Long memberId);
    @Modifying
    @Query("UPDATE NotificationHistoryEntity n SET n.read=true WHERE n.id = :id")
    void updateNotificationIsRead(@Param("id") Long id);

    Long countByMemberIdAndReadFalse(Long memberId);
}