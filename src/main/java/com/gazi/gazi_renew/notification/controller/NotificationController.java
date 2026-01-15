package com.gazi.gazi_renew.notification.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.notification.controller.port.NotificationService;
import com.gazi.gazi_renew.notification.controller.response.NotificationHistoryResponse;
import com.gazi.gazi_renew.notification.controller.response.NotificationResponse;
import com.gazi.gazi_renew.notification.controller.response.UnreadNotificationCountResponse;
import com.gazi.gazi_renew.notification.domain.Notification;
import com.gazi.gazi_renew.notification.domain.NotificationHistory;
import com.gazi.gazi_renew.route.controller.port.MyFindRoadService;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadNotificationCreate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
@RestController
public class NotificationController {
    private final NotificationService notificationService;
    private final MyFindRoadService myFindRoadService;
    private final Response response;
    /**
     * 알림 내역 조회 API
     *
     * @param pageable 페이징 정보 (page, size, sort)
     * @return 알림 내역 목록 (페이징)
     */
    @GetMapping("")
    @Operation(summary = "알림 히스토리 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 히스트리 전체 조회 성공"),
            @ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다.")
            }
    )
    public ResponseEntity<Response.Body> getNotificationHistory(
            @Parameter(hidden = true) @PageableDefault(page = 0, size = 15, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<NotificationHistory> notificationHistories = notificationService.findAllByMemberId(pageable);
        return response.success(NotificationHistoryResponse.fromPage(notificationHistories), "알림 히스트리 전체 조회 성공", HttpStatus.OK);
    }
    /**
     * unread 알림 카운트 조회 API
     * @return unread 알림 갯수
     */
    @GetMapping("/count")
    @Operation(summary = "읽지 않은 알림 갯수 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "읽지 않은 알림 갯수 조회"),
            @ApiResponse(responseCode = "404", description = "회원이 존재하지 않습니다.")
            }
    )
    public ResponseEntity<Response.Body> getUnreadNotificationCount(){
        Long unreadCount = notificationService.countByMemberIdAndReadFalse();
        return response.success(UnreadNotificationCountResponse.from(unreadCount), "읽지 않은 알림 갯수 조회 성공", HttpStatus.OK);
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 읽음 처리 성공"),
            @ApiResponse(responseCode = "404", description = "해당 알림이 존재하지 않습니다.")
    })
    public ResponseEntity<Response.Body> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return response.success(null, "알림이 읽음 상태로 변경되었습니다.", HttpStatus.OK);
    }
    @PostMapping("/enable")
    @Operation(summary = "알림 활성화")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 활성화 성공"),
            @ApiResponse(responseCode = "400", description = "해당 id로 존재하는 MyFindRoad가 없습니다."),
            @ApiResponse(responseCode = "404", description = "해당 경로가 존재하지 않습니다."),
            @ApiResponse(responseCode = "502", description = "해당 요일에 대한 알림 설정이 이미 존재합니다")}
    )
    public ResponseEntity<Response.Body> enableNotification(@RequestBody MyFindRoadNotificationCreate request) throws JsonProcessingException {
        notificationService.saveNotificationTimes(request);
        return response.success(null, "알림 활성화 성공", HttpStatus.OK);
    }

    @PostMapping("/disable")
    @Operation(summary = "알림 비활성화")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 비활성화 성공"),
            @ApiResponse(responseCode = "404", description = "해당 경로가 존재하지 않습니다.")}
    )
    public ResponseEntity<Response.Body> disableNotification(@RequestParam Long id) {
        myFindRoadService.updateRouteNotification(id, false);
        notificationService.deleteNotificationTimes(id);
        return response.success(null, "알림 비활성화 성공", HttpStatus.OK);
    }

    @GetMapping("/settings")
    @Operation(summary = "알림 상세 설정 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 상세 설정 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 경로가 존재하지 않습니다.")}
    )
    public ResponseEntity<Response.Body> getNotificationSettings(@RequestParam Long myPathId) {
        MyFindRoad myFindRoad = myFindRoadService.getRouteById(myPathId);
        List<Notification> notifications = notificationService.getNotificationTimes(myPathId);
        return response.success(NotificationResponse.fromList(myFindRoad, notifications), "알림 상세 설정 조회 성공", HttpStatus.OK);
    }

    @PostMapping("/update")
    @Operation(summary = "알림 상세 설정 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 상세 설정 수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 경로가 존재하지 않습니다.")}
    )
    public ResponseEntity<Response.Body> updateNotification(@RequestBody MyFindRoadNotificationCreate request) throws JsonProcessingException {
        MyFindRoad myFindRoad = myFindRoadService.getRouteById(request.getMyPathId());
        List<Notification> notifications = notificationService.updateNotificationTimes(request);
        return response.success(NotificationResponse.fromList(myFindRoad, notifications), "알림 상세 설정 수정 성공", HttpStatus.OK);
    }

    @GetMapping("/path-id")
    @Operation(summary = "알림 경로 ID 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 경로 ID 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 알림이 존재하지 않습니다.")}
    )
    public ResponseEntity<Response.Body> getNotificationPathId(@RequestParam Long notificationId) {
        Long pathId = notificationService.getPathId(notificationId);
        return response.success(pathId, "알림 경로 ID 조회 성공", HttpStatus.OK);
    }
}
