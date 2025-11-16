package com.gazi.gazi_renew.admin.controller;

import com.gazi.gazi_renew.admin.controller.port.AdminNotificationService;
import com.gazi.gazi_renew.admin.controller.response.AdminNoticeResponse;
import com.gazi.gazi_renew.admin.domain.AdminNotice;
import com.gazi.gazi_renew.admin.domain.dto.AdminNoticeCreate;
import com.gazi.gazi_renew.admin.domain.dto.AdminNoticeUpdate;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.issue.controller.response.MyCommentSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notices")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminNotificationController {
    private final Response response;
    private final AdminNotificationService adminNotificationService;
    @Operation(summary = "관리자용 공지사항 전체 조회")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "관리자용 공지사항 전체 조회 완료",
            headers = @Header(name = AUTHORIZATION, description = "Access Token"),
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AdminNoticeResponse.class)))})
    @GetMapping("")
    public ResponseEntity<Response.Body> getNotifications() {
        List<AdminNotice> notifications = adminNotificationService.getNotifications();
        return response.success(AdminNoticeResponse.fromList(notifications));
    }
    @Operation(summary = "관리자용 공지사항 개별 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자용 공지사항 개별 조회 완료",
                    headers = @Header(name = AUTHORIZATION, description = "Access Token"),
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AdminNoticeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "공지사항을 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{noticeId}")
    public ResponseEntity<Response.Body> getNotificationByNoticeId(@PathVariable Long noticeId) {
        AdminNotice adminNotice = adminNotificationService.getNotificationByNoticeId(noticeId);
        return response.success(AdminNoticeResponse.from(adminNotice));
    }

    @Operation(summary = "관리자용 공지사항 등록")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "관리자용 공지사항 등록 완료",
            headers = @Header(name = AUTHORIZATION, description = "Access Token"),
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AdminNoticeResponse.class)))})
    @PostMapping("")
    public ResponseEntity<Response.Body> saveNotification(@RequestBody AdminNoticeCreate adminNoticeCreate) {
        AdminNotice adminNotice = adminNotificationService.saveNotification(adminNoticeCreate);
        return response.success(AdminNoticeResponse.from(adminNotice));
    }
    @Operation(summary = "관리자용 공지사항 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자용 공지사항 삭제 완료",
                    headers = @Header(name = AUTHORIZATION, description = "Access Token"),
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AdminNoticeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "공지사항을 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Response.Body> deleteNotificationByNoticeId(@PathVariable Long noticeId) {
        adminNotificationService.deleteNotificationByNoticeId(noticeId);
        return response.success();
    }
    @Operation(summary = "관리자용 공지사항 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자용 공지사항 수정 완료",
                    headers = @Header(name = AUTHORIZATION, description = "Access Token"),
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AdminNoticeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "공지사항을 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PatchMapping("")
    public ResponseEntity<Response.Body> updateNotification(@RequestBody AdminNoticeUpdate adminNoticeUpdate) {
        adminNotificationService.updateNotification(adminNoticeUpdate);
        return response.success();
    }

}
