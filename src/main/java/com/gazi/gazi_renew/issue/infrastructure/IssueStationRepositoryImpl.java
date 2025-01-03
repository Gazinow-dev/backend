package com.gazi.gazi_renew.issue.infrastructure;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.IssueStation;
import com.gazi.gazi_renew.issue.infrastructure.entity.IssueEntity;
import com.gazi.gazi_renew.issue.infrastructure.entity.IssueStationEntity;
import com.gazi.gazi_renew.issue.infrastructure.jpa.IssueJpaRepository;
import com.gazi.gazi_renew.issue.infrastructure.jpa.IssueStationJpaRepository;
import com.gazi.gazi_renew.issue.service.port.IssueStationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class IssueStationRepositoryImpl implements IssueStationRepository {
    private final IssueStationJpaRepository issueStationJpaRepository;
    private final IssueJpaRepository issueJpaRepository;
    @Override
    public List<IssueStation> findAllByStationId(Long stationId) {
        return issueStationJpaRepository.findAllByStationId(stationId).stream()
                .map(IssueStationEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public void save(IssueStation issueStation) {
        Optional<IssueEntity> byId = issueJpaRepository.findById(issueStation.getIssue().getId());
        issueStationJpaRepository.save(IssueStationEntity.from(issueStation, byId.get()));
    }

    @Override
    public List<IssueStation> findAllByIssue(Issue issue) {
        return issueStationJpaRepository.findAllByIssueEntityId(issue.getId())
                .stream().map(IssueStationEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public void deleteIssueStationByIssueId(Long issueId) {
        issueStationJpaRepository.deleteByIssueEntityId(issueId);
    }

}
