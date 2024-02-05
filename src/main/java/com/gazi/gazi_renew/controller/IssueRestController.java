package com.gazi.gazi_renew.controller;

import com.gazi.gazi_renew.service.IssueService;
import com.gazi.gazi_renew.service.JsoupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
