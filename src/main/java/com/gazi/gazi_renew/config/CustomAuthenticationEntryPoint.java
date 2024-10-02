package com.gazi.gazi_renew.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.dto.Response.Body;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final HttpStatus STATUS = UNAUTHORIZED;

    private final Response responseBuilder;

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        Body body = responseBuilder.fail(
                        "로그인이 유효하지 않습니다.",
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
