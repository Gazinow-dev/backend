package com.gazi.gazi_renew.route.controller.port;

import com.gazi.gazi_renew.route.domain.MyFindRoadRequest;
import com.gazi.gazi_renew.route.controller.response.MyFindRoadResponse;
import com.gazi.gazi_renew.common.controller.response.Response;
import org.springframework.http.ResponseEntity;

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
