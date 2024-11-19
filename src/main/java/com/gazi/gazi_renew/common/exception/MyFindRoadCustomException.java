package com.gazi.gazi_renew.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MyFindRoadCustomException extends RuntimeException {
    private final MyFindRoadErrorCode myFindRoadErrorCode;
}
