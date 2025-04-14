package com.gazi.gazi_renew.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.common.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class UserEventTrackingAspect {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Around("@annotation(trackEvent)")
    public Object track(@NonNull ProceedingJoinPoint joinPoint, TrackEvent trackEvent) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();

        String eventName = trackEvent.value();
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String email = extractEmailFromRequest(request);
        long timestamp = System.currentTimeMillis();

        Map<String, Object> eventLog = new LinkedHashMap<>();
        eventLog.put("event", eventName);
        eventLog.put("uri", uri);
        eventLog.put("method", method);
        eventLog.put("email", email);
        eventLog.put("timestamp", Instant.ofEpochMilli(timestamp).toString());

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (isRequestDto(arg)) {
                eventLog.put("request", objectMapper.convertValue(arg, Map.class));
                break;
            }
        }

        try {
            Object result = joinPoint.proceed();
            eventLog.put("status", "success");
            log.info("사용자 활동 분석 : {}", objectMapper.writeValueAsString(eventLog));
            return result;
        } catch (Exception e) {
            eventLog.put("status", "error");
            eventLog.put("error", e.getMessage());
            log.info("사용자 활동 분석 : {}", objectMapper.writeValueAsString(eventLog));
            throw e;
        }
    }
    private String extractEmailFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                return authentication.getName();
            } catch (Exception e) {
                return "UNKNOWN";
            }
        }
        return "GUEST";
    }

    private boolean isRequestDto(Object arg) {
        return !(arg instanceof BindingResult) &&
                !(arg instanceof HttpServletRequest) &&
                !(arg instanceof HttpServletResponse) &&
                !(arg instanceof String) &&
                !(arg instanceof Integer) &&
                !(arg instanceof Long);
    }
}
