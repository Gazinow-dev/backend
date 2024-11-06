package com.gazi.gazi_renew.issue.infrastructure;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.Like;
import com.gazi.gazi_renew.issue.infrastructure.jpa.LikeJpaRepository;
import com.gazi.gazi_renew.issue.service.port.LikeRepository;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.infrastructure.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {
    private final LikeJpaRepository likeJpaRepository;

    @Override
    public Optional<Like> findByIssueAndMember(Issue issue, Member member) {
        return likeJpaRepository.findByIssueEntityAndMemberEntity(IssueEntity.from(issue), MemberEntity.from(member))
                .map(LikeEntity::toModel);
    }

    @Override
    public boolean existsByIssueAndMember(Issue issue, Member member) {
        return likeJpaRepository.existsByIssueEntityAndMemberEntity(IssueEntity.from(issue), MemberEntity.from(member));
    }

    @Override
    public Like save(Like like) {
        return likeJpaRepository.save(LikeEntity.from(like)).toModel();
    }

    @Override
    public void delete(Like like) {
        likeJpaRepository.delete(LikeEntity.from(like));
    }

}
