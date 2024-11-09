package com.gazi.gazi_renew.route.controller.port;

import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.controller.response.MyFindRoadResponse;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadCreate;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MyFindRoadService {
    // 경로 전체조회
    List<MyFindRoad> getRoutes();
    // 경로 조회 (내부)
    MyFindRoad getRouteById(Long id);
    // 경로 추가
    Long addRoute(MyFindRoadCreate myFindRoadCreate);
    // 경로 삭제
    void deleteRoute(Long id);
    // 경로 알림 상태 변경
    void updateRouteNotification(Long id, Boolean enabled);

    List<MyFindRoad> getRoutesByMember(Long memberId);
}
