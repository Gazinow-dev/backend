package com.gazi.gazi_renew.issue.controller.port;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.issue.domain.IssueCreate;
import com.gazi.gazi_renew.issue.domain.IssueDetail;
import com.gazi.gazi_renew.issue.domain.IssueUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface IssueService {
    // 웹 크롤링

    // 이슈 저장
    boolean addIssue(IssueCreate issueCreate) throws JsonProcessingException;
    // 이슈 조회
    IssueDetail getIssue(Long id);
    // 이슈 전체조회
    Page<Issue> getIssues(Pageable pageable);
    // 이슈 필터조회
    Page<Issue> getLineByIssues(String line,Pageable pageable);

    void updateIssueContent(IssueUpdate issueUpdate);
    //인기 이슈 조회
    List<Issue> getPopularIssues();
}
