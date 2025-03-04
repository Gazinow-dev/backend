package com.gazi.gazi_renew.mock.issue;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueLine;
import com.gazi.gazi_renew.issue.domain.dto.IssueStationDetail;
import com.gazi.gazi_renew.issue.service.port.IssueRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FakeIssueRepository implements IssueRepository {
    private final AtomicLong autoGeneratedId = new AtomicLong(0);
    private final List<Issue> data = new ArrayList<>();
    private final List<IssueLine> issueLineList = new ArrayList<>();
    @Override
    public boolean existsByCrawlingNo(String crawlingNo) {
        return data.stream().anyMatch(issue -> issue.getCrawlingNo().equals(crawlingNo));
    }

    @Override
    public List<IssueStationDetail> findTopIssuesByLikesCount(int likesCount, Pageable pageable) {
        // 필터링하여 likesCount보다 큰 이슈만 선택
        List<Issue> filteredIssues = data.stream()
                .filter(issue -> issue.getLikeCount() >= likesCount)
                .sorted((i1, i2) -> Integer.compare(i2.getLikeCount(), i1.getLikeCount())) // likesCount 순으로 내림차순 정렬
                .collect(Collectors.toList());

        // 페이지네이션 적용
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredIssues.size());

        return start > end ? new ArrayList<>() : filteredIssues.subList(start, end).stream().map(IssueStationDetail::fromIssue).collect(Collectors.toList());
    }

    @Override
    public Page<IssueStationDetail> getIssueByLineName(String lineName, Pageable pageable) {
        List<IssueStationDetail> issueStationDetailList = data.stream().filter(issue -> issue.getTitle().equals(lineName)).map(IssueStationDetail::fromIssue)
                .collect(Collectors.toList());
        return new PageImpl<>(issueStationDetailList, pageable, data.size());
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
    public Page<IssueStationDetail> findAll(Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), data.size());

        List<IssueStationDetail> pagedIssues = data.subList(start, end).stream().map(IssueStationDetail::fromIssue)
                .collect(Collectors.toList());

        return new PageImpl<>(pagedIssues, pageable, data.size());
    }

    @Override
    public List<IssueStationDetail> getIssueById(Long id) {
        return data.stream().filter(issue -> issue.getId().equals(id)).map(IssueStationDetail::fromIssue)
                .collect(Collectors.toList());
    }

    @Override
    public void updateIssue(Issue issue) {
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

    @Override
    public void deleteIssue(Long id) {
        data.removeIf(issue -> issue.getId().equals(id));
    }

    @Override
    public void updateStartDateAndExpireDate(Long id, LocalDateTime startDate, LocalDateTime expireDate) {
        Issue issue = data.stream()
                .filter(existingIssue -> existingIssue.getId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException());
        data.remove(issue);

        Issue updatedIssue = Issue.builder()
                .id(issue.getId())
                .title(issue.getTitle())
                .content(issue.getContent())
                .startDate(startDate)
                .expireDate(expireDate)
                .secretCode(issue.getSecretCode())
                .keyword(issue.getKeyword())
                .latestNo(issue.getLatestNo())
                .likeCount(issue.getLikeCount())
                .build();
        data.add(updatedIssue);
    }

    @Override
    public Optional<Issue> findByIssueKey(String issueKey) {
        Optional<Issue> optionalIssue = data.stream().filter(existingIssue -> existingIssue.getIssueKey().equals(issueKey))
                .findFirst();
        return optionalIssue;
    }
    @Override
    public Optional<Issue> findById(Long id) {
        return data.stream().filter(issue -> issue.getId().equals(id)).findFirst();
    }
}