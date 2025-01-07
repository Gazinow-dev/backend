package com.gazi.gazi_renew.admin.infrastructure;

import com.gazi.gazi_renew.admin.domain.Penalty;
import com.gazi.gazi_renew.admin.infrastructure.entity.PenaltyEntity;
import com.gazi.gazi_renew.admin.infrastructure.jpa.PenaltyJpaRepository;
import com.gazi.gazi_renew.admin.service.port.PenaltyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PenaltyRepositoryImpl implements PenaltyRepository {
    private final PenaltyJpaRepository penaltyJpaRepository;
    @Override
    public Penalty findOrCreatePenalty(Long reportedMemberId) {
        Optional<Penalty> penalty = penaltyJpaRepository.findByMemberId(reportedMemberId).map(PenaltyEntity::toModel);
        if (penalty.isEmpty()) {
            return penaltyJpaRepository.save(PenaltyEntity.from(Penalty.from(reportedMemberId))).toModel();
        }
        return penalty.get();
    }
    @Override
    public void updatePenalty(Penalty penalty) {
        penaltyJpaRepository.updatePenalty(penalty.getPenaltyId(), penalty.getStartDate(), penalty.getExpireDate());
    }
}
