package com.gazi.gazi_renew.admin.infrastructure.entity;

import com.gazi.gazi_renew.admin.domain.Penalty;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "penalty")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PenaltyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "penalty_id")
    private Long id;
    private Long memberId;
    private LocalDateTime startDate;
    private LocalDateTime expireDate;

    public static PenaltyEntity from(Penalty penalty) {
        PenaltyEntity penaltyEntity = new PenaltyEntity();
        penaltyEntity.id = penalty.getPenaltyId();
        penaltyEntity.memberId = penalty.getMemberId();
        penaltyEntity.startDate = penalty.getStartDate();
        penaltyEntity.expireDate = penalty.getExpireDate();

        return penaltyEntity;
    }

    public Penalty toModel() {
        return Penalty.builder()
                .penaltyId(id)
                .memberId(memberId)
                .startDate(startDate)
                .expireDate(expireDate)
                .build();
    }
}
