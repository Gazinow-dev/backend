package com.gazi.gazi_renew.mock.admin;

import com.gazi.gazi_renew.admin.domain.Penalty;
import com.gazi.gazi_renew.admin.domain.Report;
import com.gazi.gazi_renew.admin.service.port.PenaltyRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class FakePenaltyRepository implements PenaltyRepository {
    private final AtomicLong autoGeneratedId = new AtomicLong(0);
    private final List<Penalty> data = new ArrayList<>();

    @Override
    public Penalty findOrCreatePenalty(Long reportedMemberId) {
        Penalty penalty1 = data.stream().filter(penalty -> penalty.getMemberId().equals(reportedMemberId))
                .findFirst()
                .orElse(null);

        if (penalty1==null) {
            return Penalty.builder()
                    .penaltyId(autoGeneratedId.incrementAndGet())
                    .memberId(reportedMemberId)
                    .startDate(LocalDateTime.now())
                    .expireDate(LocalDateTime.now())
                    .build();
        }
        return penalty1;
    }

    @Override
    public void updatePenalty(Penalty penalty) {
        data.removeIf(existingPenalty -> Objects.equals(existingPenalty.getPenaltyId(), penalty.getPenaltyId()));

        Penalty result = Penalty.builder()
                .penaltyId(autoGeneratedId.incrementAndGet())
                .memberId(penalty.getMemberId())
                .startDate(LocalDateTime.now())
                .expireDate(LocalDateTime.now())
                .build();

        data.add(result);
    }
}
