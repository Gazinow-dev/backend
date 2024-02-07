package com.gazi.gazi_renew.repository;

import com.gazi.gazi_renew.domain.Issue;
import com.gazi.gazi_renew.domain.Like;
import com.gazi.gazi_renew.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByIssueAndMember(Issue issue, Member member);

    boolean existsByIssueAndMember(Issue issue, Member member);
}
