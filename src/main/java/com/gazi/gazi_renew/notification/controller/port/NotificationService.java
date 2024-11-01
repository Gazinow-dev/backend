package com.gazi.gazi_renew.notification.controller.port;

import com.gazi.gazi_renew.route.domain.MyFindRoadNotificationRequest;
import com.gazi.gazi_renew.common.controller.response.Response;
import org.springframework.http.ResponseEntity;

public interface NotificationService {
    ResponseEntity<Response.Body> saveNotificationTimes(MyFindRoadNotificationRequest request);

    ResponseEntity<Response.Body> getNotificationTimes(Long myPathId);

    ResponseEntity<Response.Body> deleteNotificationTimes(Long myPathId);

    ResponseEntity<Response.Body> updateNotificationTimes(MyFindRoadNotificationRequest request);

    ResponseEntity<Response.Body> getPathId(Long notificationId);
}
