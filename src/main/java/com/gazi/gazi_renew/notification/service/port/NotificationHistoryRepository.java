package com.gazi.gazi_renew.notification.service.port;

import com.gazi.gazi_renew.notification.domain.NotificationHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationHistoryRepository {
    NotificationHistory save(NotificationHistory notificationHistory);

    Page<NotificationHistory> findAllByMemberId(Long memberId, Pageable pageable);

    void updateNotificationIsRead(Long notificationId);
}
