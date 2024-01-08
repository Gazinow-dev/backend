package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.dto.MyFindRoadRequest;
import com.gazi.gazi_renew.dto.Response;
import org.springframework.http.ResponseEntity;

public interface MyFindRoadService {
    // 경로 추가
    ResponseEntity<Response.Body> addRoute(MyFindRoadRequest request);

    // 경로 삭제
    ResponseEntity<Response.Body> deleteRoute(Long id);
}
