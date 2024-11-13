package com.gazi.gazi_renew.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.notification.domain.Notification;
import com.gazi.gazi_renew.route.domain.MyFindRoad;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FakeRedisUtilService{
    private final Map<String, String> keyValueStore = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> hashStore = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public FakeRedisUtilService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String getData(String key) {
        return keyValueStore.get(key);
    }

    public void setDataExpire(String key, String value, long duration) {
        keyValueStore.put(key, value);
    }

    public void setRefreshToken(String username, String refreshToken, long expirationTime) {
        String key = "RT:" + username;
        keyValueStore.put(key, refreshToken);
    }

    public void deleteToken(String key) {
        keyValueStore.remove(key);
    }

    public void addToBlacklist(String token, long expiration) {
        keyValueStore.put(token, "logout");
    }

    public void saveNotificationTimes(List<Notification> notificationList, MyFindRoad myFindRoad) throws JsonProcessingException {
        List<Map<String, Object>> notificationJsonList = new ArrayList<>();
        for (Notification notification : notificationList) {
            Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("day", notification.getDayOfWeek());
            notificationData.put("from_time", notification.getFromTime().toString());
            notificationData.put("to_time", notification.getToTime().toString());
            notificationJsonList.add(notificationData);
        }

        String fieldName = myFindRoad.getMember().getId().toString();
        String notificationJsonArray = convertListToJson(notificationJsonList);

        hashStore.computeIfAbsent("user_notifications", k -> new ConcurrentHashMap<>())
                .put(fieldName, notificationJsonArray);
    }

    public void deleteNotification(String fieldName) {
        Map<String, String> userNotifications = hashStore.get("user_notifications");
        if (userNotifications != null) {
            userNotifications.remove(fieldName);
        }
    }

    private String convertListToJson(List<Map<String, Object>> notificationJsonList) throws JsonProcessingException {
        return objectMapper.writeValueAsString(notificationJsonList);
    }
}
