package com.gazi.gazi_renew.common.aspect;

import com.gazi.gazi_renew.common.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    @Around("execution(* com.gazi.gazi_renew..*Controller.*(..))")
    public Object logHttpRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attr == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attr.getRequest();
        HttpServletResponse response = attr.getResponse();

        String uri = request.getRequestURI();
        String method = request.getMethod();
        long start = System.currentTimeMillis();

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            int status = response != null ? response.getStatus() : 500;

            log.error("ERROR [{}] {} - {}ms | status={} ",
                    method, uri, duration, status,  e);
            throw e;
        }
        long duration = System.currentTimeMillis() - start;
        int status = response != null ? response.getStatus() : 200;

        log.info("REQUEST [{}] {} - {}ms | status={}", method, uri, duration, status);
        return result;
    }
}
