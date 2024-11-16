package com.gazi.gazi_renew.issue.controller;

import com.gazi.gazi_renew.common.controller.BaseController;
import com.gazi.gazi_renew.issue.controller.response.IssueResponse;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.issue.controller.port.IssueService;
import com.gazi.gazi_renew.issue.domain.dto.IssueDetail;
import com.gazi.gazi_renew.issue.domain.dto.IssueUpdate;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/issue")
public class IssueRestController extends BaseController {
    private final IssueService issueService;
    private final Response response;

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/get")
    public ResponseEntity<Response.Body> getIssue(@RequestParam(name="id") Long id){
        IssueDetail issue = issueService.getIssue(id);
        return response.success(IssueResponse.fromIssueDetail(issue), "이슈 조회 성공", HttpStatus.OK);
    }

    @GetMapping("/get_all")
    public ResponseEntity<Response.Body> getIssues(@Parameter(hidden = true) @PageableDefault(page = 0, size = 15, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable){
        Page<Issue> issuePage = issueService.getIssues(pageable);
        return response.success(IssueResponse.fromIssueDetailPage(issuePage), "이슈 전체 조회 성공", HttpStatus.OK);
    }

    @GetMapping("/get_line")
    public ResponseEntity<Response.Body> getLineByIssues(@RequestParam(name="line") String line,
                                                         @Parameter(hidden = true) @PageableDefault(page = 0, size = 15, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable){
        Page<Issue> sortedIssues = issueService.getLineByIssues(line, pageable);
        return response.success(IssueResponse.fromIssueDetailPage(sortedIssues), "line" + "이슈 조회 성공", HttpStatus.OK);
    }
    @GetMapping("/get_popular")
    public ResponseEntity<Response.Body> getPopularIssue() {
        List<Issue> issueList = issueService.getPopularIssues();
        return response.success(issueList, "인기 이슈 조회 성공", HttpStatus.OK);
    }


    @PostMapping("/update_content")
    public ResponseEntity<Response.Body> updateContent(@RequestBody IssueUpdate issueUpdate){
        issueService.updateIssueContent(issueUpdate);
        return response.success(" 내용 수정 성공");
    }
}
