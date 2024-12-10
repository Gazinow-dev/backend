package com.gazi.gazi_renew.member.service.port;

import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.domain.RecentSearch;
import com.gazi.gazi_renew.member.infrastructure.MemberEntity;
import com.gazi.gazi_renew.member.infrastructure.RecentSearchEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RecentSearchRepository {

    List<RecentSearch> findAllByMemberOrderByModifiedAtDesc(Long memberId);
    Optional<RecentSearch> findByMemberAndStationLineAndStationName(Long memberId, String stationLine, String stationName);

    Optional<RecentSearch> findByIdAndMember(Long recentSearchID, Long memberId);

    void delete(RecentSearch recentSearch);
    void deleteByMemberId(Long memberId);

    RecentSearch save(RecentSearch recentSearch);

    void updateModifiedAt(Long id, LocalDateTime modifiedAt);
}
