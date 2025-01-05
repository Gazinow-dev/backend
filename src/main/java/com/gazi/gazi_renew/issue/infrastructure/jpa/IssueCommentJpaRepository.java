package com.gazi.gazi_renew.issue.infrastructure.jpa;

import com.gazi.gazi_renew.issue.infrastructure.entity.IssueCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueCommentJpaRepository extends JpaRepository<IssueCommentEntity, Long> {
    @Query("SELECT c FROM IssueCommentEntity c JOIN FETCH c.issueEntity WHERE c.memberId = :memberId ORDER BY c.createdAt DESC")
    List<IssueCommentEntity> findByMemberIdOrderByCreatedAt(@Param("memberId") Long memberId);
    @Modifying
    @Query("UPDATE IssueCommentEntity c SET c.issueCommentContent=:issueCommentContent WHERE c.id=:issueCommentId")
    void updateIssueCommentEntityContent(Long issueCommentId, String issueCommentContent);
    int countByIssueEntityId(Long issueEntityId);
    @Query("SELECT c FROM IssueCommentEntity c JOIN FETCH c.issueEntity i WHERE i.id = :issueEntityId ORDER BY c.createdAt DESC")
    List<IssueCommentEntity> findByIssueEntityIdOrderByCreatedAt(@Param("issueEntityId") Long issueEntityId);

}