package com.gazi.gazi_renew.issue.infrastructure;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.Like;
import com.gazi.gazi_renew.issue.infrastructure.jpa.LikeJpaRepository;
import com.gazi.gazi_renew.issue.service.port.LikeRepository;
import com.gazi.gazi_renew.user.infrastructure.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {
    private final LikeJpaRepository likeJpaRepository;

    @Override
    public Optional<Like> findByIssueAndMember(IssueEntity issueEntity, MemberEntity memberEntity) {
        return likeJpaRepository.findByIssueAndMember()
    }

    @Override
    public boolean existsByIssueAndMember(Issue issue, Member member) {
        return false;
    }
}
