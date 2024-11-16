package com.gazi.gazi_renew.issue.infrastructure;

import com.gazi.gazi_renew.issue.domain.Like;
import com.gazi.gazi_renew.issue.infrastructure.jpa.LikeJpaRepository;
import com.gazi.gazi_renew.issue.service.port.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {
    private final LikeJpaRepository likeJpaRepository;

    @Override
    public Optional<Like> findByIssueAndMember(Long issueId, Long memberId) {
        return likeJpaRepository.findByIssueEntityIdAndMemberEntityId(issueId, memberId)
                .map(LikeEntity::toModel);
    }

    @Override
    public boolean existsByIssueAndMember(Long issueId, Long memberId) {
        return likeJpaRepository.existsByIssueEntityIdAndMemberEntityId(issueId, memberId);
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
