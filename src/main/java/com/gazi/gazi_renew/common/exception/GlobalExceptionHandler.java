package com.gazi.gazi_renew.common.exception;

import com.gazi.gazi_renew.common.controller.response.Response;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Response response;

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Response.Body> handleEntityNotFoundException(EntityNotFoundException e) {
        return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response.Body> handleGeneralException(Exception e) {
        return response.fail("알 수 없는 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Response.Body> handleBadCredentialsException(BadCredentialsException e) {
        return response.fail("비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Response.Body> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        return response.fail(errorCode.getDetail(), errorCode.getHttpStatus());
    }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Response.Body> handleIllegalStateException(IllegalStateException e) {
        return response.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
