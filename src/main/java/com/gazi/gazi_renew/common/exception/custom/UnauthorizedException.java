package com.gazi.gazi_renew.common.exception.custom;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException() {
        super("인증되지 않은 요청입니다.");  // 기본 메시지 설정
    }
}
