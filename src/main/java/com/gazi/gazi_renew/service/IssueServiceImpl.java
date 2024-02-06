package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.domain.Issue;
import com.gazi.gazi_renew.dto.IssueRequest;
import com.gazi.gazi_renew.dto.IssueResponse;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.repository.IssueRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService{

    private final IssueRepository issueRepository;
    private final Response response;

    @Override
    public ResponseEntity<Response.Body> addIssue(IssueRequest dto) {
        // 포맷터
        LocalDate date = LocalDate.parse(dto.getDate(), DateTimeFormatter.ISO_DATE);

        Issue issue = Issue.builder()
                .date(date)
                .title(dto.getTitle())
                .content(dto.getContent())
                .line(dto.getLine())
                .build();

        issueRepository.save(issue);
        return null;
    }

    @Override
    public ResponseEntity<Response.Body> getIssue(Long id) {
        Issue issue = issueRepository.findById(id).orElseThrow( () -> new EntityNotFoundException("해당 id로 존재하는 이슈를 찾을 수 없습니다."));
        IssueResponse issueResponse = IssueResponse.builder()
                .id(issue.getId())
                .title(issue.getTitle())
                .content(issue.getContent())
                .date(issue.getDate().toString())
                .line(issue.getLine())
                .build();
        return response.success(issueResponse,"이슈 조회 성공", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Response.Body> getIssues(Pageable pageable) {
        Page<Issue> issuePage = issueRepository.findAll(pageable);
        Page<IssueResponse> issueResponsePage = getPostDtoPage(issuePage);

        return response.success(issueResponsePage,"이슈 전체 조회 성공", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Response.Body> getLineByIssues(String line, Pageable pageable) {
        Page<Issue> issuePage = issueRepository.findALlByLine(line);
        Page<IssueResponse> issueResponsePage = getPostDtoPage(issuePage);

        return response.success(issueResponsePage,"line"+"이슈 조회 성공", HttpStatus.OK);
    }
    public Page<IssueResponse> getPostDtoPage( Page<Issue> issuePage) {

        Page<IssueResponse> issueResponsePage = issuePage.map(m ->
                IssueResponse.builder()
                        .id(m.getId())
                        .title(m.getTitle())
                        .content(m.getContent())
                        .date(m.getDate().toString())
                        .line(m.getLine())
                        .build());

        return issueResponsePage;
    }



}
