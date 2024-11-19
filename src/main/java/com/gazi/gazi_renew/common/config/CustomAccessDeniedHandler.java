package com.gazi.gazi_renew.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.common.controller.response.Response.Body;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final HttpStatus STATUS = FORBIDDEN;

    private final Response responseBuilder;

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        Body body = responseBuilder.fail(
                        "접근 권한이 없습니다.",
                        STATUS
                )
                .getBody();

        response.setContentType(APPLICATION_JSON_VALUE);

        response.setCharacterEncoding("UTF-8");

        response.setStatus(STATUS.value());

        response.getWriter()
                .write(objectMapper.writeValueAsString(body));
    }

}