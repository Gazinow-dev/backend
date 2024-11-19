package com.gazi.gazi_renew.issue.infrastructure.jpa;

import com.gazi.gazi_renew.issue.infrastructure.IssueLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IssueLineJpaRepository extends JpaRepository<IssueLineEntity, Long> {
    @Query("SELECT i FROM IssueLineEntity i JOIN FETCH i.issueEntity WHERE i.lineEntity.id = :lineEntityId")
    List<IssueLineEntity> findByLineEntityId(@Param("lineEntityId") Long lineEntityId);

    @Query("SELECT i FROM IssueLineEntity i JOIN FETCH i.issueEntity WHERE i.issueEntity.id = :issueEntityId")
    List<IssueLineEntity> findByIssueEntityId(@Param("issueEntityId") Long issueEntityId);
}
