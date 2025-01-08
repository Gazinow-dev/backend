package com.gazi.gazi_renew.common.exception;

import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.common.exception.CustomException;
import com.gazi.gazi_renew.common.exception.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Response response;

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Response.Body> handleEntityNotFoundException(EntityNotFoundException e) {
        return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
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
    @ExceptionHandler(MyFindRoadCustomException.class)
    public ResponseEntity<Response.Body> handleMyFindRoadCustomException(MyFindRoadCustomException e) {
        MyFindRoadErrorCode errorCode = e.getMyFindRoadErrorCode();
        return response.fail(errorCode.getDetail(), errorCode.getHttpStatus(), errorCode.getErrorCodeName());
    }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Response.Body> handleIllegalStateException(IllegalStateException e) {
        return response.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response.Body> handleValidationException(MethodArgumentNotValidException e) {
        // 모든 필드 에러를 순회하며 에러 메시지를 리스트로 수집
        List<String> errorMessages = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        // 에러 메시지 리스트를 하나의 문자열로 조합 (필요에 따라 개별적으로 반환 가능)
        String combinedErrorMessage = String.join(", ", errorMessages);

        return response.fail(combinedErrorMessage, HttpStatus.BAD_REQUEST);
    }

}
