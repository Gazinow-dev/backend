package com.gazi.gazi_renew.route.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.common.controller.BaseController;
import com.gazi.gazi_renew.notification.controller.response.NotificationResponse;
import com.gazi.gazi_renew.notification.domain.Notification;
import com.gazi.gazi_renew.route.controller.response.MyFindRoadResponse;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadNotificationCreate;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.route.controller.port.MyFindRoadService;
import com.gazi.gazi_renew.notification.controller.port.NotificationService;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadCreate;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@RequestMapping("/api/v1/my_find_road")
@RestController
public class MyFindRoadController extends BaseController {
    private final MyFindRoadService myFindRoadService;
    private final NotificationService notificationService;
    private final Response response;
    @GetMapping("/get_roads")
    @Operation(summary = "내 경로 전체 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "마이 길찾기 조회 성공"),
            @ApiResponse(responseCode = "404", description = "0호선으로된 데이터 정보를 찾을 수 없습니다.")}
    )
    public ResponseEntity<Response.Body> getRoutes() {
        List<MyFindRoad> myFindRoadList = myFindRoadService.getRoutes();
        return response.success(MyFindRoadResponse.fromList(myFindRoadList), "마이 길찾기 조회 성공", HttpStatus.OK);
    }
    @Hidden
    @GetMapping("/get_roads/by_id")
    public ResponseEntity<Response.Body> getRoutesByMember(@RequestParam Long memberId) {
        List<MyFindRoad> myFindRoadList = myFindRoadService.getRoutesByMember(memberId);
        return response.success(MyFindRoadResponse.fromList(myFindRoadList), "마이 길찾기 조회 성공", HttpStatus.OK);
    }

    @Operation(summary = "내 경로 저장")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "내 경로 저장 성공",
                    headers = @Header(name = AUTHORIZATION, description = "Access Token"
                    )
            ),
            @ApiResponse(
                    responseCode = "401", description = "회원이 존재하지 않습니다."
            )
    })
    @PostMapping("/add_route")
    public ResponseEntity<Response.Body> addRoute(@RequestBody MyFindRoadCreate myFindRoadCreate) {
        Long id = myFindRoadService.addRoute(myFindRoadCreate);
        return response.success(id, "데이터 저장완료", HttpStatus.CREATED);
    }
    @DeleteMapping("/delete_route")
    @Operation(summary = "내 경로 삭제")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "내 경로 삭제 성공",
                    headers = @Header(name = AUTHORIZATION, description = "Access Token"
                    )
            ),
            @ApiResponse(
                    responseCode = "401", description = "회원이 존재하지 않습니다."
            ),
            @ApiResponse(
                    responseCode = "400", description = "해당 id로 존재하는 MyFindRoad가 없습니다."
            )
    })
    public ResponseEntity<Response.Body> deleteRoute(@RequestParam Long id) {
        notificationService.deleteNotificationTimes(id);
        myFindRoadService.deleteRoute(id);
        return response.success("삭제 완료");
    }
}
