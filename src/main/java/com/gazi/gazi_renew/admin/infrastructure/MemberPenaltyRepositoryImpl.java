package com.gazi.gazi_renew.admin.infrastructure;

import com.gazi.gazi_renew.admin.infrastructure.jpa.MemberPenaltyJpaRepository;
import com.gazi.gazi_renew.admin.service.port.MemberPenaltyRepository;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class MemberPenaltyRepositoryImpl implements MemberPenaltyRepository {
    private final MemberPenaltyJpaRepository memberPenaltyJpaRepository;

    @Override
    public boolean isMemberRestricted(Long memberId) {
        return memberPenaltyJpaRepository.existsByMemberIdAndStartDateBeforeAndExpireDateAfter(memberId, LocalDateTime.now(), LocalDateTime.now());
    }
}
