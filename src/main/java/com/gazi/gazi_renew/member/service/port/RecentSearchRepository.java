package com.gazi.gazi_renew.member.service.port;

import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.domain.RecentSearch;
import com.gazi.gazi_renew.member.infrastructure.MemberEntity;
import com.gazi.gazi_renew.member.infrastructure.RecentSearchEntity;

import java.util.List;
import java.util.Optional;

public interface RecentSearchRepository {

    List<RecentSearch> findAllByMemberOrderByModifiedAtDesc(Member member);
    Optional<RecentSearch> findByMemberAndStationLineAndStationName(Member member, String stationLine, String stationName);

    Optional<RecentSearch> findByIdAndMember(Long recentSearchID, Member member);

    void delete(RecentSearch recentSearch);

    RecentSearch save(RecentSearch recentSearch);
}
