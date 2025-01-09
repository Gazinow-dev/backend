package com.gazi.gazi_renew.admin.service.port;

public interface MemberPenaltyRepository {
    boolean isMemberRestricted(Long memberId);
}
