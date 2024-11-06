package com.gazi.gazi_renew.issue.service.port;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.Like;
import com.gazi.gazi_renew.member.domain.Member;

import java.util.Optional;

public interface LikeRepository {
    Optional<Like> findByIssueAndMember(Issue issue, Member member);

    boolean existsByIssueAndMember(Issue issue, Member member);

    Like save(Like like);

    void delete(Like like);
}
