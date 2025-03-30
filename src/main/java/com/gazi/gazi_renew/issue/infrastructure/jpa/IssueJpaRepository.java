package com.gazi.gazi_renew.issue.infrastructure.jpa;

import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.issue.infrastructure.entity.IssueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


@Repository
public interface IssueJpaRepository extends JpaRepository<IssueEntity, Long>{
    @Modifying
    @Query("UPDATE IssueEntity i SET i.content = :content, i.title = :title, i.startDate = :startDate, i.expireDate = :expireDate, i.keyword = :keyword WHERE i.id = :id")
    void updateIssue(@Param("id") Long id, @Param("title") String title, @Param("content") String content, @Param("startDate") LocalDateTime startDate, @Param("expireDate") LocalDateTime expireDate, @Param("keyword") IssueKeyword keyword);
    @Modifying
    @Query("UPDATE IssueEntity i SET i.startDate = :startDate, i.expireDate = :expireDate WHERE i.id = :id")
    void updateStartDateAndExpireDate(@Param("id") Long id, @Param("startDate") LocalDateTime startDate, @Param("expireDate") LocalDateTime expireDate);
    @Modifying
    @Query("UPDATE IssueEntity i SET i.likeCount = :likeCount WHERE i.id = :id")
    void updateLikeCount(@Param("id") Long id, @Param("likeCount")int likeCount);
    boolean existsByCrawlingNo(String crawlingNo);
}
