package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.dto.MyFindRoadNotificationRequest;
import com.gazi.gazi_renew.dto.Response;
import org.springframework.http.ResponseEntity;

public interface NotificationService {
    ResponseEntity<Response.Body> saveNotificationTimes(MyFindRoadNotificationRequest request);

    ResponseEntity<Response.Body> getNotificationTimes(Long myPathId);

    ResponseEntity<Response.Body> deleteNotificationTimes(Long myPathId);

    ResponseEntity<Response.Body> updateNotificationTimes(MyFindRoadNotificationRequest request);

    ResponseEntity<Response.Body> pushNotifications(Long issueId);
}
