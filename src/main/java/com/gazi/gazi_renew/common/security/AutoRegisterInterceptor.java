package com.gazi.gazi_renew.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gazi.gazi_renew.common.exception.ErrorCode;
import com.gazi.gazi_renew.issue.domain.dto.InternalIssueCreate;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AutoRegisterInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("POST".equalsIgnoreCase(request.getMethod()) && request.getRequestURI().startsWith("/api/v1/issue/internal-issues")) {
            objectMapper.registerModule(new JavaTimeModule());
            log.info("Request URI: {}", request.getRequestURI());
            log.info("Request Method: {}", request.getMethod());
            log.info("Request Class: {}", request.getClass().getName());
            String bodyFromInputStream = readBodyFromInputStream(request);

            log.info("Body from InputStream: {}", bodyFromInputStream);


            ContentCachingRequestWrapper cachingRequest = (ContentCachingRequestWrapper) request;

            byte[] contentBytes = cachingRequest.getContentAsByteArray();
            log.debug("Raw content bytes length: {}", contentBytes.length);
            String body = getRequestBody(cachingRequest);

            // AutomationIssueCreate 변환
            InternalIssueCreate requestBody = objectMapper.readValue(body, InternalIssueCreate.class);

            // lines 변환
            List<String> updatedLines = convertLines(requestBody.getLines());
            List<String> updatedLocations = convertStations(requestBody.getLocations());

            if (requestBody.getProcessRange() && (updatedLocations.size() > 2 || updatedLines.size() > 1)) {
                throw ErrorCode.throwInvalidSubwayRangeException();
            }
            if (requestBody.getLocations().isEmpty()) {
                throw ErrorCode.throwInvalidLocationsException();
            }
            if (requestBody.getLines().isEmpty()) {
                throw ErrorCode.throwInvalidLinesException();
            }


            // 변환된 객체를 다시 JSON으로 변환 후 설정
            InternalIssueCreate updatedRequestBody = InternalIssueCreate.builder()
                    .title(requestBody.getTitle())
                    .content(requestBody.getContent())
                    .keyword(requestBody.getKeyword())
                    .startDate(requestBody.getStartDate())
                    .expireDate(requestBody.getExpireDate())
                    .lines(updatedLines)
                    .locations(updatedLocations)
                    .issueKey(requestBody.getIssueKey())
                    .processRange(requestBody.getProcessRange())
                    .lineInfoAvailable(requestBody.getLineInfoAvailable())
                    .crawlingNo(requestBody.getCrawlingNo())
                    .build();

            request.setAttribute("internalIssueCreate", updatedRequestBody);

        }
        return true; // 다음 핸들러로 진행
    }
    private List<String> convertStations(List<String> locations) {
        return locations.stream()
                .map(location -> {
                    // 특정 조건에서는 그대로 반환
                    if (location.equals("서울역")) {
                        return location; // "서울역"은 그대로 유지
                    }
                    // '역'으로 끝나면 제거, 그렇지 않으면 그대로 반환
                    if (location.endsWith("역")) {
                        return location.substring(0, location.length() - 1);
                    }
                    return location; // '역'으로 끝나지 않으면 그대로 반환
                })
                .collect(Collectors.toList());
    }

    private List<String> convertLines(List<String> lines) {
        // "1호선" → "수도권 1호선"으로 변환 (이미 '수도권'이 포함된 경우 제외, 경의중앙선 제외)
        return lines.stream()
                .map(line -> {
                    if (line.equals("경의중앙선")) {
                        return line; // 경의중앙선은 그대로 유지
                    }
                    return line.startsWith("수도권") ? line : "수도권 " + line; // 수도권이 없는 경우만 추가
                })
                .collect(Collectors.toList());
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        try {
            return new String(content, request.getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to read request body", e);
        }
    }


//    private String getRequestBody(ContentCachingRequestWrapper request) throws IOException {
//        request.setCharacterEncoding("UTF-8");
//
//        StringBuilder stringBuilder = new StringBuilder();
//        BufferedReader reader = request.getReader();
//        String line;
//        while ((line = reader.readLine()) != null) {
//            stringBuilder.append(line);
//        }
//        return stringBuilder.toString();
//    }
private String readBodyFromInputStream(HttpServletRequest request) {
    try {
        ServletInputStream inputStream = request.getInputStream();
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    } catch (Exception e) {
        log.error("Error reading from input stream", e);
        return "";
    }
}

}
