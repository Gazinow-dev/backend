package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.dto.MyFindRoadRequest;
import com.gazi.gazi_renew.dto.Response;
import org.springframework.http.ResponseEntity;

public interface MyFindRoadService {
    // 경로 전체조회
    ResponseEntity<Response.Body> getRoutes();
    // 경로 조회
    ResponseEntity<Response.Body> getRoute(Long id);
    // 경로 추가
    ResponseEntity<Response.Body> addRoute(MyFindRoadRequest request);
    // 경로 삭제
    ResponseEntity<Response.Body> deleteRoute(Long id);
}
