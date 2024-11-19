package com.gazi.gazi_renew.common.controller.port;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.issue.infrastructure.IssueRedisDto;
import com.gazi.gazi_renew.notification.domain.Notification;
import com.gazi.gazi_renew.route.domain.MyFindRoad;

import java.util.List;

public interface RedisUtilService {
    String getData(String key);

    void setDataExpire(String key, String value, long duration);

    void setRefreshToken(String username, String refreshToken, long expirationTime);

    void deleteToken(String key);

    void addToBlacklist(String token, long expiration);

    void saveNotificationTimes(List<Notification> notificationList, MyFindRoad myFindRoad) throws JsonProcessingException;

    void deleteNotification(String fieldName);

    void addIssueToRedis(String key, String hashKey, IssueRedisDto issueRedisDto) throws JsonProcessingException;
}
