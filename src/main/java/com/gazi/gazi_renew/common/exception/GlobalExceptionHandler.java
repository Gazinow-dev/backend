package com.gazi.gazi_renew.common.exception;

import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.common.exception.custom.DuplicateIssueException;
import com.gazi.gazi_renew.common.exception.custom.UnauthorizedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
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

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Response.Body> handleUnauthorizedException(UnauthorizedException e) {
        return response.fail(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DuplicateIssueException.class)
    public ResponseEntity<Response.Body> handleDuplicateIssueException(DuplicateIssueException e) {
        return response.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response.Body> handleGeneralException(Exception e) {
        return response.fail("알 수 없는 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
