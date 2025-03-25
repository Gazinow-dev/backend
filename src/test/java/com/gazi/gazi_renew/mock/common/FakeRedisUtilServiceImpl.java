package com.gazi.gazi_renew.mock.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.common.controller.port.RedisUtilService;
import com.gazi.gazi_renew.issue.infrastructure.IssueRedisDto;
import com.gazi.gazi_renew.notification.domain.Notification;
import com.gazi.gazi_renew.route.domain.MyFindRoad;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public class FakeRedisUtilServiceImpl implements RedisUtilService {
    private final Map<String, String> keyValueStore = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> hashStore = new ConcurrentHashMap<>();
    private final Set<String> forbiddenWords = ConcurrentHashMap.newKeySet();
    private final ObjectMapper objectMapper;
    public FakeRedisUtilServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String getData(String key) {
        return "mw310@naver.com";
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

    @Override
    public void saveNotificationTimes(List<Notification> notificationList, Long myFindRoadPathId) throws JsonProcessingException {
        List<Map<String, Object>> notificationJsonList = new ArrayList<>();
        for (Notification notification : notificationList) {
            Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("day", notification.getDayOfWeek());
            notificationData.put("from_time", notification.getFromTime().toString());
            notificationData.put("to_time", notification.getToTime().toString());
            notificationJsonList.add(notificationData);
        }

        String fieldName = myFindRoadPathId.toString();
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
    @Override
    public void addIssueToRedis(String hashKey, String issueKey, IssueRedisDto issueRedisDto) throws JsonProcessingException {
        String issueValueAsString = objectMapper.writeValueAsString(issueRedisDto);

        hashStore.computeIfAbsent(hashKey, k -> new ConcurrentHashMap<>())
                .put(issueKey, issueValueAsString);
    }

    @Override
    public Map<String, List<Map<String, Object>>> getAllUserNotifications() throws JsonProcessingException {
        return null;
    }
    @Override
    public boolean containsForbiddenWord(String nickname) {
        return forbiddenWords.stream().anyMatch(nickname::contains);
    }

}
