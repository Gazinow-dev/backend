package com.gazi.gazi_renew.common.exception.custom;

public class DuplicateIssueException extends RuntimeException {
    public DuplicateIssueException(String message) {
        super(message);
    }

    public DuplicateIssueException() {
        super("중복된 이슈가 존재합니다.");  // 기본 메시지 설정
    }
}
