package com.gazi.gazi_renew.issue.infrastructure.jpa;

import com.gazi.gazi_renew.issue.infrastructure.IssueEntity;
import com.gazi.gazi_renew.station.infrastructure.LineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IssueJpaRepository extends JpaRepository<IssueEntity, Long> {
    boolean existsByCrawlingNo(String crawlingNo);

    // Like의 수가 N개 이상인 Issue를 Like의 수에 따라 내림차순으로 정렬하여 반환
    @Transactional(readOnly=true)
    @Query("SELECT i FROM IssueEntity i WHERE SIZE(i.likes) >= :likesCount ORDER BY SIZE(i.likes) DESC")
    List<IssueEntity> findTopIssuesByLikesCount(@Param("likesCount") int likesCount, Pageable pageable);
}