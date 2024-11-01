package com.gazi.gazi_renew.issue.controller;

import com.gazi.gazi_renew.common.controller.BaseController;
import com.gazi.gazi_renew.issue.domain.IssueRequest;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.issue.controller.port.IssueService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/issue")
public class IssueRestController extends BaseController {
    private final IssueService issueService;

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/get")
    public ResponseEntity<Response.Body> getIssue(@RequestParam(name="id") Long id){
        return issueService.getIssue(id);
    }

    @GetMapping("/get_all")
    public ResponseEntity<Response.Body> getIssues(@Parameter(hidden = true) @PageableDefault(page = 0, size = 15, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable){
        return issueService.getIssues(pageable);
    }

    @GetMapping("/get_line")
    public ResponseEntity<Response.Body> getLineByIssues(@RequestParam(name="line") String line,
                                                         @Parameter(hidden = true) @PageableDefault(page = 0, size = 15, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable){
        return issueService.getLineByIssues(line,pageable);
    }
    @GetMapping("/get_popular")
    public ResponseEntity<Response.Body> getPopularIssue(){
        return issueService.getPopularIssues();
    }

    @PostMapping("/update_content")
    public ResponseEntity<Response.Body> updateContent(@RequestBody IssueRequest.updateContentDto dto){
        return issueService.updateIssueContent(dto);
    }
}
