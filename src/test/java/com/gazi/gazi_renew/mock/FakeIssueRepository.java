package com.gazi.gazi_renew.mock;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.service.port.IssueRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FakeIssueRepository implements IssueRepository {
    private final AtomicLong autoGeneratedId = new AtomicLong(0);
    private final List<Issue> data = new ArrayList<>();
    @Override
    public boolean existsByCrawlingNo(String crawlingNo) {
        return data.stream().anyMatch(issue -> issue.getCrawlingNo().equals(crawlingNo));
    }

    @Override
    public List<Issue> findTopIssuesByLikesCount(int likesCount, Pageable pageable) {
        // 필터링하여 likesCount보다 큰 이슈만 선택
        List<Issue> filteredIssues = data.stream()
                .filter(issue -> issue.getLikeCount() >= likesCount)
                .sorted((i1, i2) -> Integer.compare(i2.getLikeCount(), i1.getLikeCount())) // likesCount 순으로 내림차순 정렬
                .collect(Collectors.toList());

        // 페이지네이션 적용
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredIssues.size());

        return start > end ? new ArrayList<>() : filteredIssues.subList(start, end);
    }

    @Override
    public Issue save(Issue issue) {
        if (issue.getId() == null || issue.getId() == 0) {
            Issue createIssue = Issue.builder()
                    .id(autoGeneratedId.incrementAndGet())
                    .title(issue.getTitle())
                    .content(issue.getContent())
                    .startDate(issue.getStartDate())
                    .expireDate(issue.getExpireDate())
                    .secretCode(issue.getSecretCode())
                    .keyword(issue.getKeyword())
                    .latestNo(issue.getLatestNo())
                    .likeCount(issue.getLikeCount())
                    .build();
            data.add(createIssue);
            return createIssue;
        }
        else{
            data.removeIf(issue1 -> Objects.equals(issue1.getId(), issue.getId()));
            data.add(issue);
            return issue;
        }
    }

    @Override
    public Page<Issue> findAll(Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), data.size());

        List<Issue> pagedIssues = data.subList(start, end);

        return new PageImpl<>(pagedIssues, pageable, data.size());
    }

    @Override
    public Optional<Issue> findById(Long id) {
        return data.stream().filter(issue -> issue.getId().equals(id)).findFirst();
    }

    @Override
    public void updateContent(Issue issue) {
        data.removeIf(existingIssue -> existingIssue.getId().equals(issue.getId()));

        Issue updatedIssue = Issue.builder()
                .id(issue.getId())
                .title(issue.getTitle())
                .content(issue.getContent())
                .startDate(issue.getStartDate())
                .expireDate(issue.getExpireDate())
                .secretCode(issue.getSecretCode())
                .keyword(issue.getKeyword())
                .latestNo(issue.getLatestNo())
                .likeCount(issue.getLikeCount())
                .build();
        data.add(updatedIssue);

    }
    @Override
    public void updateLikeCount(Issue issue) {
        data.removeIf(existingIssue -> existingIssue.getId().equals(issue.getId()));

        Issue updatedIssue = Issue.builder()
                .id(issue.getId())
                .title(issue.getTitle())
                .content(issue.getContent())
                .startDate(issue.getStartDate())
                .expireDate(issue.getExpireDate())
                .secretCode(issue.getSecretCode())
                .keyword(issue.getKeyword())
                .latestNo(issue.getLatestNo())
                .likeCount(issue.getLikeCount())
                .build();
        data.add(updatedIssue);
    }

    @Override
    public void flush() {
    }
}