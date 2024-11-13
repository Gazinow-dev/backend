package com.gazi.gazi_renew.issue.infrastructure.jpa;

import com.gazi.gazi_renew.issue.infrastructure.IssueEntity;
import com.gazi.gazi_renew.station.infrastructure.LineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
    @Query("SELECT i FROM IssueEntity i WHERE SIZE(i.likeEntities) >= :likesCount ORDER BY SIZE(i.likeEntities) DESC")
    List<IssueEntity> findTopIssuesByLikesCount(@Param("likesCount") int likesCount, Pageable pageable);
    @Modifying
    @Query("UPDATE IssueEntity i SET i.content = :content WHERE i.id = :id")
    void updateContent(@Param("id") Long id, @Param("content") String content);

    @Query("SELECT i FROM IssueEntity i JOIN FETCH i.stationEntities s WHERE s.id = :stationId")
    List<IssueEntity> findAllByStationId(@Param("stationId") Long stationId);
}
