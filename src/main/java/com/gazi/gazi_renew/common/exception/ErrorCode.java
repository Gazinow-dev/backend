package com.gazi.gazi_renew.common.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    DUPLICATE_NICKNAME(CONFLICT, "중복된 닉네임입니다."),
    INVALID_REFRESH_TOKEN(UNAUTHORIZED, "Refresh Token 정보가 일치하지 않습니다."),
    INVALID_REFRESH_TOKEN_MISMATCH(BAD_REQUEST, "Refresh Token 정보가 일치하지 않습니다."),
    INVALID_VERIFICATION_CODE(UNAUTHORIZED, "인증코드가 일치하지 않습니다."),
    DUPLICATE_ISSUE(BAD_REQUEST, "이미 해당 데이터가 존재합니다.");
    private final HttpStatus httpStatus;
    private final String detail;

    public static CustomException throwDuplicateNicknameException() {
        return new CustomException(DUPLICATE_NICKNAME);
    }

    public static CustomException InvalidRefreshToken() {
        return new CustomException(INVALID_REFRESH_TOKEN);
    }

    public static CustomException throwInvalidVerificationCode() {
        return new CustomException(INVALID_VERIFICATION_CODE);
    }

    public static CustomException throwDuplicateIssueException() {
        return new CustomException(DUPLICATE_ISSUE);
    }

    public static CustomException throwInvalidRefreshTokenMissMatch() {
        return new CustomException(INVALID_REFRESH_TOKEN_MISMATCH);
    }
}
