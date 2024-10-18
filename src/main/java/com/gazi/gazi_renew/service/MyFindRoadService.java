package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.dto.MyFindRoadRequest;
import com.gazi.gazi_renew.dto.MyFindRoadResponse;
import com.gazi.gazi_renew.dto.Response;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MyFindRoadService {
    // 경로 전체조회
    ResponseEntity<Response.Body> getRoutes();
    // 경로 조회 (내부)
    MyFindRoadResponse getRouteById(Long id);
    // 경로 추가
    ResponseEntity<Response.Body> addRoute(MyFindRoadRequest request);
    // 경로 삭제
    ResponseEntity<Response.Body> deleteRoute(Long id);
    // 경로 알림 상태 변경
    ResponseEntity<Response.Body> updateRouteNotification(Long id, Boolean enabled);

    ResponseEntity<Response.Body> getRoutesByMember(Long memberId);
}
