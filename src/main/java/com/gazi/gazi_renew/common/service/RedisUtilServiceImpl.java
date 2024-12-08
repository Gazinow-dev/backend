package com.gazi.gazi_renew.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.common.controller.port.RedisUtilService;
import com.gazi.gazi_renew.issue.infrastructure.IssueRedisDto;
import com.gazi.gazi_renew.notification.domain.Notification;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisUtilServiceImpl implements RedisUtilService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public String getData(String key) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public void setDataExpire(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    public void setRefreshToken(String username, String refreshToken, long expirationTime) {
        String key = "RT:" + username;
        redisTemplate.opsForValue().set(key, refreshToken, expirationTime, TimeUnit.MILLISECONDS);
    }

    public void deleteToken(String key) {
        redisTemplate.delete(key);
    }

    public void addToBlacklist(String token, long expiration) {
        redisTemplate.opsForValue().set(token, "logout", expiration, TimeUnit.MILLISECONDS);
    }

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
        System.out.println(notificationJsonArray);

        // redis에 저장
        redisTemplate.opsForHash().put("user_notifications", fieldName, notificationJsonArray);
    }
    /**
     * Redis에서 모든 user_notifications 데이터를 가져온다
     * @return Map<String, List<Map<String, Object>>> - 모든 사용자 알림 데이터
     * @throws JsonProcessingException JSON 역직렬화 오류
     */
    public Map<String, List<Map<String, Object>>> getAllUserNotifications() throws JsonProcessingException {
        // Redis에서 "user_notifications" 해시의 모든 데이터 가져오기
        Map<Object, Object> redisData = redisTemplate.opsForHash().entries("user_notifications");

        // 변환된 결과를 저장할 Map
        Map<String, List<Map<String, Object>>> userNotifications = new HashMap<>();

        for (Map.Entry<Object, Object> entry : redisData.entrySet()) {
            String memberId = (String) entry.getKey();
            String notificationJson = (String) entry.getValue();

            // JSON 문자열을 List<Map<String, Object>>로 변환
            List<Map<String, Object>> notifications = objectMapper.readValue(notificationJson, List.class);
            userNotifications.put(memberId, notifications);
        }

        return userNotifications;
    }
    public void deleteNotification(String fieldName) {
        redisTemplate.opsForHash().delete("user_notifications", fieldName);
    }
    private String convertListToJson(List<Map<String, Object>> notificationJsonList) throws JsonProcessingException {
        return objectMapper.writeValueAsString(notificationJsonList);
    }
    @Override
    public void addIssueToRedis(String hashKey, String issueKey, IssueRedisDto issueRedisDto) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonValue = objectMapper.writeValueAsString(issueRedisDto);

        redisTemplate.opsForHash().put(hashKey, issueKey, jsonValue);
    }
    private List<Map<String, Object>> convertJsonToList(String jsonArray) throws JsonProcessingException {
        return objectMapper.readValue(jsonArray, List.class);
    }
}
