package com.gazi.gazi_renew.issue.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.common.controller.BaseController;
import com.gazi.gazi_renew.issue.controller.response.IssueResponse;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.issue.controller.port.IssueService;
import com.gazi.gazi_renew.issue.domain.dto.ExternalIssueCreate;
import com.gazi.gazi_renew.issue.domain.dto.InternalIssueCreate;
import com.gazi.gazi_renew.issue.domain.dto.IssueStationDetail;
import com.gazi.gazi_renew.issue.domain.dto.IssueUpdate;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/issue")
public class IssueRestController extends BaseController {
    private final IssueService issueService;
    private final Response response;

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/get")
    public ResponseEntity<Response.Body> getIssue(@RequestParam(name="id") Long id){
        List<IssueStationDetail> issueStationDetailList = issueService.getIssue(id);
        return response.success(IssueResponse.fromIssueDetail(issueStationDetailList), "이슈 조회 성공", HttpStatus.OK);
    }

    @GetMapping("/get_all")
    public ResponseEntity<Response.Body> getIssues(@Parameter(hidden = true) @PageableDefault(page = 0, size = 15, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable){
        Page<IssueStationDetail> issuePage = issueService.getIssues(pageable);
        return response.success(IssueResponse.fromIssueDetailPage(issuePage), "이슈 전체 조회 성공", HttpStatus.OK);
    }

    @GetMapping("/get_line")
    public ResponseEntity<Response.Body> getLineByIssues(@RequestParam(name="line") String line,
                                                         @Parameter(hidden = true) @PageableDefault(page = 0, size = 15, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable){
        Page<IssueStationDetail> sortedIssues = issueService.getLineByIssues(line, pageable);
        return response.success(IssueResponse.fromIssueDetailPage(sortedIssues), "line" + "이슈 조회 성공", HttpStatus.OK);
    }
    @GetMapping("/get_popular")
    public ResponseEntity<Response.Body> getPopularIssue() {
        List<IssueStationDetail> issueList = issueService.getPopularIssues();
        return response.success(IssueResponse.fromPopularIssueDetail(issueList), "인기 이슈 조회 성공", HttpStatus.OK);
    }
    @PatchMapping("")
    public ResponseEntity<Response.Body> updateIssue(@RequestBody IssueUpdate issueUpdate){
        issueService.updateIssue(issueUpdate);
        return response.success(" 이슈 수정 성공");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Response.Body> deleteIssue(@PathVariable Long id){
        issueService.deleteIssue(id);
        return response.success(" 이슈 삭제 성공");
    }
    @PostMapping("/internal-issues")
    public ResponseEntity<Response.Body> autoRegisterInternalIssue(HttpServletRequest request) throws JsonProcessingException {
        InternalIssueCreate processedRequest = (InternalIssueCreate) request.getAttribute("internalIssueCreate");

        issueService.autoRegisterInternalIssue(processedRequest);

        return response.success(" 이슈 등록 성공");
    }
    @PostMapping("/external-issues")
    public ResponseEntity<Response.Body> autoRegisterExternalIssue(@RequestBody ExternalIssueCreate externalIssueCreate) throws JsonProcessingException {
        issueService.autoRegisterExternalIssue(externalIssueCreate);

        return response.success(" 이슈 등록 성공");
    }
}
