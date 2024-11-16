package com.gazi.gazi_renew.issue.service.port;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.Like;
import com.gazi.gazi_renew.member.domain.Member;

import java.util.Optional;

public interface LikeRepository {
    Optional<Like> findByIssueAndMember(Long issueId, Long memberId);

    boolean existsByIssueAndMember(Long issueId, Long memberId);

    Like save(Like like);

    void delete(Like like);
}
