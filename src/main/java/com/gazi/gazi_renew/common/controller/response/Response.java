package com.gazi.gazi_renew.common.controller.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;

@Component
public class Response {
    @Getter
    @Builder
    public static class Body {
        private int state;
        private String result;
        private String message;
        private Object data;
        private Object error;
        private String errorCodeName;
    }

    /**
     * <p> 메세지와 데이터를 포함한 성공 응답을 반환한다.</p>
     * <pre>
     *     {
     *         "state" : 200,
     *         "result" : success,
     *         "message" : message,
     *         "data" : [{data1}, {data2}...],
     *         "error" : []
     *     }
     * </pre>
     *
     * @param msg 응답 바디 message 필드에 포함될 정보
     * @return 응답 객체
     */
    public ResponseEntity<Body> success(Object data, String msg, HttpStatus status) {
        Body body = Body.builder()
                .state(status.value())
                .result("success")
                .message(msg)
                .data(data)
                .error(Collections.emptyList())
                .build();
        return new ResponseEntity<>(body, status);
    }

    /**
     * <p> 메세지만 가진 성공 응답을 반환한다.</p>
     * <pre>
     *     {
     *         "state" : 200,
     *         "result" : success,
     *         "message" : message,
     *         "data" : [],
     *         "error" : []
     *     }
     * </pre>
     *
     * @param msg 응답 바디 message 필드에 포함될 정보
     * @return 응답 객체
     */
    public ResponseEntity<Body> success(String msg) {
        return success(Collections.emptyList(), msg, HttpStatus.OK);
    }
    public ResponseEntity<Body> createSuccess(String msg) {
        return success(Collections.emptyList(), msg, HttpStatus.CREATED);
    }

    /**
     * <p> 데이터만 가진 성공 응답을 반환한다.</p>
     * <pre>
     *     {
     *         "state" : 200,
     *         "result" : success,
     *         "message" : null,
     *         "data" : [{data1}, {data2}...],
     *         "error" : []
     *     }
     * </pre>
     *
     * @param data 응답 바디 data 필드에 포함될 정보
     * @return 응답 객체
     */
    public ResponseEntity<Body> success(Object data) {
        return success(data, null, HttpStatus.OK);
    }

    /**
     * <p> 성공 응답만 반환한다. </p>
     * <pre>
     *     {
     *         "state" : 200,
     *         "result" : success,
     *         "message" : null,
     *         "data" : [],
     *         "error" : []
     *     }
     * </pre>
     *
     * @return 응답 객체
     */
    public ResponseEntity<Body> success() {
        return success(Collections.emptyList(), null, HttpStatus.OK);
    }


    public ResponseEntity<Body> fail(Object data, String msg, HttpStatus status) {
        Body body = Body.builder()
                .state(status.value())
                .data(data)
                .result("fail")
                .message(msg)
                .error(Collections.emptyList())
                .build();
        return new ResponseEntity<>(body, status);
    }
    public ResponseEntity<Body> fail(Object data, String msg, HttpStatus status, String errorCodeName) {
        Body body = Body.builder()
                .state(status.value())
                .data(data)
                .result("fail")
                .message(msg)
                .error(Collections.emptyList())
                .errorCodeName(errorCodeName)
                .build();
        return new ResponseEntity<>(body, status);
    }

    /**
     * <p> 메세지를 가진 실패 응답을 반환한다. </p>
     * <pre>
     *     {
     *         "state" : HttpStatus Code,
     *         "result" : fail,
     *         "message" : message,
     *         "data" : [],
     *         "error" : [{error1}, {error2}...]
     *     }
     * </pre>
     *
     * @param msg    응답 바디 message 필드에 포함될 정보
     * @param status 응답 바디 status 필드에 포함될 응답 상태 코드
     * @return 응답 객체
     */
    public ResponseEntity<Body> fail(String msg, HttpStatus status) {
        return fail(Collections.emptyList(), msg, status);
    }
    /**
     * <p> 메세지를 가진 실패 응답을 반환한다. </p>
     * <pre>
     *     {
     *         "state" : HttpStatus Code,
     *         "result" : fail,
     *         "message" : message,
     *         "data" : [],
     *         "error" : [{error1}, {error2}...]
     *         "errorCodeName" : "001
     *     }
     * </pre>
     *
     * @param msg    응답 바디 message 필드에 포함될 정보
     * @param status 응답 바디 status 필드에 포함될 응답 상태 코드
     * @return 응답 객체
     */
    public ResponseEntity<Body> fail(String msg, HttpStatus status, String errorCodeName) {
        return fail(Collections.emptyList(), msg, status, errorCodeName);
    }
}
