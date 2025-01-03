package com.gazi.gazi_renew.issue.infrastructure.jpa;

import com.gazi.gazi_renew.issue.infrastructure.entity.IssueCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface IssueCommentJpaRepository extends JpaRepository<IssueCommentEntity, Long> {

    List<IssueCommentEntity> findByMemberId(Long memberId);
    @Modifying
    @Query("UPDATE IssueCommentEntity c SET c.issueCommentContent=:issueCommentContent WHERE c.id=:issueCommentId")
    IssueCommentEntity updateIssueCommentEntityContent(Long issueCommentId, String issueCommentContent);
}