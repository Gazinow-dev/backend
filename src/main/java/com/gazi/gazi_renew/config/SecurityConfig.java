package com.gazi.gazi_renew.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.dto.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;
    private final Response response;
    private final ObjectMapper objectMapper;


    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedOriginPatterns(Arrays.asList("*")); // ⭐️ 허용할 origin
            config.setAllowCredentials(true);
            return config;
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(CsrfConfigurer::disable)
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(Customizer.withDefaults())
                .logout(logout -> logout.disable()) // 로그아웃 사용 X
                .formLogin(formLogin -> formLogin.disable()) // 폼 로그인 사용 X
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/v1/like", "/api/v1/member/logout", "/api/v1/member/reissue", "/api/v1/member/change_nickname", "/api/v1/member/change_password"
                                        , "/api/v1/member/delete_member", "/api/v1/my_find_road/*", "/api/v1/recentSearch", "/api/v1/recentSearch/*", "/api/v1/member/fcm-token").authenticated() // 요청에 대해 인증 필요
                                .anyRequest().permitAll()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        exception ->
                                exception.authenticationEntryPoint(
                                                new CustomAuthenticationEntryPoint(
                                                        response,
                                                        objectMapper
                                                )
                                        )
                                        .accessDeniedHandler(
                                                new CustomAccessDeniedHandler(
                                                        response,
                                                        objectMapper
                                                )
                                        )
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

