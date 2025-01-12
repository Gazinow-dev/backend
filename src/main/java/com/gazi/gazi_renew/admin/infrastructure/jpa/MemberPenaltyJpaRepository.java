package com.gazi.gazi_renew.admin.infrastructure.jpa;

import com.gazi.gazi_renew.admin.infrastructure.entity.PenaltyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface MemberPenaltyJpaRepository extends JpaRepository<PenaltyEntity , Long> {
    boolean existsByMemberIdAndStartDateBeforeAndExpireDateAfter(Long memberId, LocalDateTime startDate, LocalDateTime endDate);
}
