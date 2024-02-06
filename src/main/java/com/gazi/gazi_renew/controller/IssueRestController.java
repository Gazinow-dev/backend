package com.gazi.gazi_renew.controller;

import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.service.IssueService;
import com.gazi.gazi_renew.service.JsoupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/issue")
public class IssueRestController {
    private final JsoupService jsoupService;
    private final IssueService issueService;

    @PostMapping
    public void sendEmail() throws Exception {
        jsoupService.getData();
    }

    @GetMapping
    public ResponseEntity<Response.Body> getIssue(@RequestParam(name="id") Long id){
        return issueService.getIssue(id);
    }

    @GetMapping
    public ResponseEntity<Response.Body> getIssues(@PageableDefault(page = 0, size = 15, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
        return issueService.getIssues(pageable);
    }

    @GetMapping
    public ResponseEntity<Response.Body> getLineByIssues(@RequestParam(name="line") String line,
                                                         @PageableDefault(page = 0, size = 15, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
        return issueService.getLineByIssues(line,pageable);
    }
}
