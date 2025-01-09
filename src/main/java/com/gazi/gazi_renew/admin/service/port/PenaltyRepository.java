package com.gazi.gazi_renew.admin.service.port;

import com.gazi.gazi_renew.admin.domain.Penalty;

public interface PenaltyRepository {
    Penalty findOrCreatePenalty(Long reportedMemberId);

    void updatePenalty(Penalty penalty);
}
