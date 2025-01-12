package com.gazi.gazi_renew.mock.security;

import org.springframework.security.crypto.password.PasswordEncoder;

// 테스트 환경에서만 사용할 PasswordEncoder 구현 클래스
public class TestPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return "encoded_" + rawPassword;  // 단순하게 인코딩을 흉내내기 위해 접두사를 붙임
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(encode(rawPassword));
    }
}
