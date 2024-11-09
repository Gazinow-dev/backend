package com.gazi.gazi_renew.notification.controller.port;

import com.gazi.gazi_renew.route.domain.MyFindRoadNotification;
import com.gazi.gazi_renew.common.controller.response.Response;
import org.springframework.http.ResponseEntity;

public interface NotificationService {
    ResponseEntity<Response.Body> saveNotificationTimes(MyFindRoadNotification request);

    ResponseEntity<Response.Body> getNotificationTimes(Long myPathId);

    ResponseEntity<Response.Body> deleteNotificationTimes(Long myPathId);

    ResponseEntity<Response.Body> updateNotificationTimes(MyFindRoadNotification request);

    ResponseEntity<Response.Body> getPathId(Long notificationId);
}
