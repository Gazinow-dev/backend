package com.gazi.gazi_renew.issue.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.issue.domain.dto.IssueCreate;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.controller.port.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/issue")
@Controller
public class IssueController {

    private final IssueService issueService;
    private final Response response;
    @PostMapping("/add")
    public ResponseEntity addIssue(@ModelAttribute("dto") IssueCreate issueCreate) throws JsonProcessingException {
        boolean isAdded = issueService.addIssue(issueCreate);
        if (isAdded) {
            return response.success("이슈가 성공적으로 추가되었습니다.");
        } else {
            return response.fail("이슈 추가에 실패했습니다.", HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping()
    public String writeIssue(Model model, Issue dto,
                             @RequestParam(name = "title")String title,
                             @RequestParam(name = "content")String content,
                             @RequestParam(name = "crawlingNo")String crawlingNo,
                             @RequestParam(name = "latestNo")int latestNo)
    {
        model.addAttribute("title", title);
        model.addAttribute("content", content);
        model.addAttribute("crawlingNo", crawlingNo);
        model.addAttribute("dto", dto);
        model.addAttribute("keyword", IssueKeyword.values());
        model.addAttribute("latestNo",latestNo);
        return "writeIssue";
    }
}
