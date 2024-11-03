package com.gazi.gazi_renew.issue.infrastructure.jpa;

import com.gazi.gazi_renew.issue.infrastructure.IssueEntity;
import com.gazi.gazi_renew.issue.infrastructure.LikeEntity;
import com.gazi.gazi_renew.user.infrastructure.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeJpaRepository extends JpaRepository<LikeEntity, Long> {
    Optional<LikeEntity> findByIssueAndMember(IssueEntity issueEntity, MemberEntity memberEntity);

    boolean existsByIssueAndMember(IssueEntity issueEntity, MemberEntity memberEntity);
}
