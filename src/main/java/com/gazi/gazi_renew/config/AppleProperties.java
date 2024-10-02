package com.gazi.gazi_renew.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "oauth.apple.url")
@Getter
@Setter
public class AppleProperties {
    private String auth;
    private String clientId;
    private String keyId;
    private String redirectUrl;
    private String teamId;
    private String privateKey;
}