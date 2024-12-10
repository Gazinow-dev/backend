package com.gazi.gazi_renew.notification.service.port;

import com.gazi.gazi_renew.notification.domain.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    List<Notification> findByMyFindRoadPathId(Long myFindRoadPathId);

    void deleteByMyFindRoadId(Long myFindRoadId);

    List<Notification> saveAll(List<Notification> notificationList);

    Optional<Notification> findById(Long notificationId);
}
