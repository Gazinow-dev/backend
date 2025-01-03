package com.gazi.gazi_renew.issue.infrastructure.jpa;

import com.gazi.gazi_renew.issue.infrastructure.entity.IssueStationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IssueStationJpaRepository extends JpaRepository<IssueStationEntity, Long> {
    @Query("SELECT i FROM IssueStationEntity i JOIN FETCH i.issueEntity WHERE i.stationEntity.id = :stationId")
    List<IssueStationEntity> findAllByStationId(Long stationId);
    @Query("SELECT i FROM IssueStationEntity i JOIN FETCH i.stationEntity WHERE i.issueEntity.id = :issueEntityId")
    List<IssueStationEntity> findAllByIssueEntityId(@Param("issueEntityId") Long issueEntityId);

    void deleteByIssueEntityId(Long issueEntityId);
}
