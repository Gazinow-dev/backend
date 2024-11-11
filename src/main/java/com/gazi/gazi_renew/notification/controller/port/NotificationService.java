package com.gazi.gazi_renew.notification.controller.port;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.notification.domain.Notification;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadNotificationCreate;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface NotificationService {
    List<Notification> saveNotificationTimes(MyFindRoadNotificationCreate request) throws JsonProcessingException;

    List<Notification> getNotificationTimes(Long myPathId);

    void deleteNotificationTimes(Long myPathId);

    List<Notification> updateNotificationTimes(MyFindRoadNotificationCreate request) throws JsonProcessingException;

    Long getPathId(Long notificationId);
}
