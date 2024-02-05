package com.gazi.gazi_renew.controller;


import com.gazi.gazi_renew.dto.IssueRequest;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.service.IssueService;
import com.gazi.gazi_renew.service.JsoupService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/issue")
@Controller
public class IssueController {

    private final JsoupService jsoupService;
    private final IssueService issueService;

    @PostMapping("/add")
    public ResponseEntity addIssue(@ModelAttribute("dto") IssueRequest dto ){
        return issueService.addIssue(dto);
    }

    @GetMapping()
    public String writeIssue(Model model,IssueRequest dto,
                             @RequestParam(name = "title")String title,
                             @RequestParam(name = "content")String content,
                             @RequestParam(name = "date")String date)
    {
        model.addAttribute("title", title);
        model.addAttribute("content", content);
        model.addAttribute("date", date);
        model.addAttribute("dto", dto);
        return "writeIssue";
    }
}
