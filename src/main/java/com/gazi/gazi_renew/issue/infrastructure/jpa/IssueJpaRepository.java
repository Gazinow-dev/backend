package com.gazi.gazi_renew.issue.infrastructure.jpa;

import com.gazi.gazi_renew.issue.infrastructure.IssueEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueJpaRepository extends JpaRepository<IssueEntity, Long> {
    boolean existsByCrawlingNo(String crawlingNo);

    // Like의 수가 N개 이상인 Issue를 Like의 수에 따라 내림차순으로 정렬하여 반환
    @Query("SELECT i FROM IssueEntity i WHERE i.likeCount >= :likesCount ORDER BY i.likeCount DESC")
    List<IssueEntity> findTopIssuesByLikesCount(@Param("likesCount") int likesCount, Pageable pageable);
    @Modifying
    @Query("UPDATE IssueEntity i SET i.content = :content, i.title = :title WHERE i.id = :id")
    void updateContentAndTitle(@Param("id") Long id, @Param("title") String title, @Param("content") String content);

//    @Query("SELECT i FROM IssueEntity i JOIN FETCH i.lineEntities s WHERE s.id = :lineId")
//    List<IssueEntity> findByLineId(@Param("lineId") Long lineId);
    @Modifying
    @Query("UPDATE IssueEntity i SET i.likeCount = :likeCount WHERE i.id = :id")
    void updateLikeCount(@Param("id") Long id, @Param("likeCount")int likeCount);
}
