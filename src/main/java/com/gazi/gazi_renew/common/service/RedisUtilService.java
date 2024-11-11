package com.gazi.gazi_renew.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.notification.domain.Notification;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
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
public class RedisUtilService {
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

    public void saveNotificationTimes(List<Notification> notificationList, MyFindRoad myFindRoad) throws JsonProcessingException {
        List<Map<String, Object>> notificationJsonList = new ArrayList<>();
        Map<String, Object> notificationData = new HashMap<>();
        for (Notification notification : notificationList) {
            notificationData.put("day", notification.getDayOfWeek());
            notificationData.put("from_time", notification.getFromTime().toString());
            notificationData.put("to_time", notification.getToTime().toString());

            notificationJsonList.add(notificationData);

            String fieldName = myFindRoad.getMember().getId().toString();

            String notificationJsonArray = convertListToJson(notificationJsonList);
            System.out.println(notificationJsonArray);

            // redis에 저장
            redisTemplate.opsForHash().put("user_notifications", fieldName, notificationJsonArray);
        }

    }
    public void deleteNotification(String fieldName) {
        redisTemplate.opsForHash().delete("user_notifications", fieldName);
    }
    private String convertListToJson(List<Map<String, Object>> notificationJsonList) throws JsonProcessingException {
        return objectMapper.writeValueAsString(notificationJsonList);
    }
}
