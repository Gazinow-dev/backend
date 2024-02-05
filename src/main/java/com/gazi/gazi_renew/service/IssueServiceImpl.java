package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.domain.Issue;
import com.gazi.gazi_renew.dto.IssueRequest;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService{

    private final IssueRepository issueRepository;

    @Override
    public ResponseEntity<Response.Body> addIssue(IssueRequest dto) {
        System.out.println(dto.getDate());
        System.out.println(dto.getLine());
        LocalDate date = LocalDate.parse(dto.getDate(), DateTimeFormatter.ISO_DATE);

        // 포맷터
        Issue issue = Issue.builder()
                .date(date)
                .title(dto.getTitle())
                .content(dto.getContent())
                .line(dto.getLine())
                .build();

        issueRepository.save(issue);
        return null;
    }



}
