package com.gazi.gazi_renew.admin.controller;

import com.gazi.gazi_renew.admin.controller.port.ReportService;
import com.gazi.gazi_renew.admin.domain.Report;
import com.gazi.gazi_renew.admin.domain.dto.ReportCreate;
import com.gazi.gazi_renew.common.controller.BaseController;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.issue.controller.response.CommentLikesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
@SecurityRequirement(name = "Bearer Authentication")
public class ReportController extends BaseController {
    public final ReportService reportService;
    private final Response response;
    @Operation(summary = "신고 처리 API")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "신고 접수 완료",
            headers = @Header(name = AUTHORIZATION, description = "Access Token")),
            @ApiResponse(responseCode = "409", description = "이미 신고 처리한 댓글입니다.")})
    @PostMapping
    public ResponseEntity<Response.Body> createReport(@Valid @RequestBody ReportCreate reportCreate) {
        reportService.createReport(reportCreate);
        return response.createSuccess("신고 접수 완료");
    }
}
