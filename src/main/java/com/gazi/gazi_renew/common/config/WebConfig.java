package com.gazi.gazi_renew.common.config;

import com.gazi.gazi_renew.common.security.AutoRegisterInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AutoRegisterInterceptor autoRegisterInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(autoRegisterInterceptor)
                .addPathPatterns("/api/v1/issue"); // 특정 엔드포인트만 대상
    }
}
