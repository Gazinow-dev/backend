package com.gazi.gazi_renew.oauth.domain;

import com.gazi.gazi_renew.oauth.domain.enums.OAuthProvider;
import org.springframework.util.MultiValueMap;

public interface OAuthLoginParams {
    OAuthProvider oAuthProvider();
    MultiValueMap<String, String> makeBody();
}