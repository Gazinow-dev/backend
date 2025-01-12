package com.gazi.gazi_renew.common.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /* 409 CONFLICT : Resource 중복 */
    DUPLICATE_NICKNAME(CONFLICT, "중복된 닉네임입니다."),
    DUPLICATE_EMAIL(CONFLICT, "이미 가입된 이메일입니다."),
    ALREADY_LIKED_ISSUE(CONFLICT, "이미 좋아요를 누른 이슈입니다."),
    ALREADY_LIKED_ISSUE_COMMENT(CONFLICT, "이미 좋아요를 누른 댓글입니다."),
    DUPLICATE_ROAD_NAME(CONFLICT, "이미 존재하는 경로 이름입니다."),
    DUPLICATE_REPORT_COMMENT(CONFLICT, "이미 신고 처리한 댓글입니다."),
    DUPLICATE_PROCESS_REPORT(CONFLICT, "이미 제제 적합/부적합 처리한 신고입니다."),
    /* 401 UNAUTHORIZED : 인증 실패 */
    INVALID_REFRESH_TOKEN(UNAUTHORIZED, "Refresh Token 정보가 일치하지 않습니다."),
    INVALID_VERIFICATION_CODE(UNAUTHORIZED, "인증코드가 일치하지 않습니다."),
    DUPLICATE_NOTIFICATION_FOR_DAY(BAD_GATEWAY, "해당 요일에 대한 알림 설정이 이미 존재합니다."),
    /* 400 BAD_REQUEST : 잘못된 요청 */
    INVALID_REFRESH_TOKEN_MISMATCH(BAD_REQUEST, "Refresh Token 정보가 일치하지 않습니다."),
    INVALID_PASSWORD(BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    INVALID_CUR_PASSWORD(BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다."),
    DUPLICATE_ISSUE(BAD_REQUEST, "이미 해당 데이터가 존재합니다."),
    FAILED_FCM_MESSAGE(BAD_REQUEST, "FCM 메시지 전송 실패"),
    NOTIFICATION_INVALID_TIME_RANGE(BAD_REQUEST, "알림 시작시간보다 종료시간이 더 빠릅니다."),
    MY_FIND_ROAD_NOT_FOUND(BAD_REQUEST, "해당 id로 존재하는 MyFindRoad가 없습니다.");


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

    public static CustomException throwInvalidPassword() {
        return new CustomException(INVALID_PASSWORD);
    }

    public static CustomException throwInvalidCurPassword() {
        return new CustomException(INVALID_CUR_PASSWORD);
    }

    public static CustomException throwDuplicateEmailException() {
        return new CustomException(DUPLICATE_EMAIL);
    }
    public static CustomException throwDuplicateLikeException() {
        return new CustomException(ALREADY_LIKED_ISSUE);
    }

    public static CustomException throwDuplicateRoadName() {
        return new CustomException(DUPLICATE_ROAD_NAME);
    }
    public static CustomException throwMyFindRoadNotFoundException() {
        return new CustomException(MY_FIND_ROAD_NOT_FOUND);
    }

    public static CustomException throwFailedFcmMessage() {
        return new CustomException(FAILED_FCM_MESSAGE);
    }

    public static CustomException throwInvalidTimeRange() {
        return new CustomException(NOTIFICATION_INVALID_TIME_RANGE);
    }
    public static CustomException throwDuplicateNotificationForDay() {
        return new CustomException(DUPLICATE_NOTIFICATION_FOR_DAY);
    }

    public static CustomException throwDuplicateCommentLikeException() {
        return new CustomException(ALREADY_LIKED_ISSUE_COMMENT);
    }

    public static CustomException throwDuplicateReportException() {
        return new CustomException(DUPLICATE_REPORT_COMMENT);
    }
    public static CustomException throwDuplicateProcessReportException() {
        return new CustomException(DUPLICATE_PROCESS_REPORT);
    }
}
