package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.dto.Response;
import org.springframework.http.ResponseEntity;

public interface MyFindLoadService {
    // 경로 추가
    ResponseEntity<Response.Body> addRoute();
    // 경로 삭제
    ResponseEntity<Response.Body> deleteRoute();
}
