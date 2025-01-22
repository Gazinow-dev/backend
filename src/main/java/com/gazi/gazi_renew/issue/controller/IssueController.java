package com.gazi.gazi_renew.issue.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.issue.controller.port.IssueManualService;
import com.gazi.gazi_renew.issue.domain.dto.IssueCreate;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
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

    private final IssueManualService issueManualService;
    private final Response response;
    @PostMapping("/add")
    public ResponseEntity addIssue(@ModelAttribute("dto") IssueCreate issueCreate) throws JsonProcessingException {
        System.out.println("Stations: " + issueCreate.getStations()); // 디버깅용 출력
        issueCreate.getStations().forEach(station -> {
            System.out.println("Line: " + station.getLine());
            System.out.println("Start Station Code: " + station.getStartStationCode());
            System.out.println("End Station Code: " + station.getEndStationCode());
            System.out.println("Keyword: " + station.getKeyword());
            System.out.println("Direction: " + station.getDirection());
        });
        boolean isAdded = issueManualService.addIssue(issueCreate);
        if (isAdded) {
            return response.success("이슈가 성공적으로 추가되었습니다.");
        } else {
            return response.fail("이슈 추가에 실패했습니다.", HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping()
    public String writeIssue(Model model, IssueCreate issueCreate,
                             @RequestParam(name = "title") String title,
                             @RequestParam(name = "content") String content,
                             @RequestParam(name = "crawlingNo") String crawlingNo,
                             @RequestParam(name = "latestNo") int latestNo) {

        model.addAttribute("title", title);
        model.addAttribute("content", content);
        model.addAttribute("crawlingNo", crawlingNo);
        model.addAttribute("dto", issueCreate);
        model.addAttribute("keyword", IssueKeyword.values());
        model.addAttribute("latestNo", latestNo);
        return "writeIssue";
    }
}
