package com.gazi.gazi_renew.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MyFindRoadErrorCode {

    DUPLICATE_ROAD_NAME(HttpStatus.CONFLICT, "이미 존재하는 경로 이름입니다.", "001"),
    DUPLICATE_ROAD_PATH(HttpStatus.CONFLICT, "이미 존재하는 경로입니다.", "002");

    private final HttpStatus httpStatus;
    private final String detail;
    private final String errorCodeName;

    public static MyFindRoadCustomException throwDuplicateRoadName() {
        return new MyFindRoadCustomException(DUPLICATE_ROAD_NAME);
    }
    public static MyFindRoadCustomException throwDuplicateRoadPath() {
        return new MyFindRoadCustomException(DUPLICATE_ROAD_PATH);
    }
}
