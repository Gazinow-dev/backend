package com.gazi.gazi_renew.issue.infrastructure.jpa;

import com.gazi.gazi_renew.issue.infrastructure.entity.IssueCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueCommentJpaRepository extends JpaRepository<IssueCommentEntity, Long> {
    @Query("SELECT c FROM IssueCommentEntity c JOIN FETCH c.issueEntity WHERE c.memberId = :memberId")
    Page<IssueCommentEntity> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);
    @Modifying
    @Query("UPDATE IssueCommentEntity c SET c.issueCommentContent=:issueCommentContent WHERE c.id=:issueCommentId")
    void updateIssueCommentEntityContent(@Param("issueCommentId")Long issueCommentId,@Param("issueCommentContent") String issueCommentContent);
    int countByIssueEntityId(Long issueEntityId);
    @Query("SELECT c FROM IssueCommentEntity c JOIN FETCH c.issueEntity i WHERE i.id = :issueEntityId")
    Page<IssueCommentEntity> findByIssueEntityId(@Param("issueEntityId") Long issueEntityId, Pageable pageable);

}