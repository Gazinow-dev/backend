package com.gazi.gazi_renew.issue.infrastructure;

import com.gazi.gazi_renew.user.infrastructure.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByIssueAndMember(Issue issue, Member member);

    boolean existsByIssueAndMember(Issue issue, Member member);
}
