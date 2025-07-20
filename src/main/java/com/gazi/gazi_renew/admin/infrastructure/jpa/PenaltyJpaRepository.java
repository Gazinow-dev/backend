package com.gazi.gazi_renew.admin.infrastructure.jpa;


import com.gazi.gazi_renew.admin.infrastructure.entity.PenaltyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PenaltyJpaRepository extends JpaRepository<PenaltyEntity, Long> {

    Optional<PenaltyEntity> findByMemberId(Long memberId);

    @Modifying
    @Query("UPDATE PenaltyEntity p SET p.startDate = :startDate , p.expireDate = :expireDate WHERE p.id =:id")
    void updatePenalty(Long id, LocalDateTime startDate, LocalDateTime expireDate);
}
