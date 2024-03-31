package com.gazi.gazi_renew.repository;

import com.gazi.gazi_renew.domain.Member;
import com.gazi.gazi_renew.domain.RecentSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecentSearchRepository extends JpaRepository<RecentSearch, Long> {
    List<RecentSearch> findAllByMemberOrderByModifiedAtDesc(Member member);
    Optional<RecentSearch> findByMemberAndStationLineAndStationName(Member member, String stationLine, String stationName);

    Optional<RecentSearch> findByIdAndMember(Long recentSearchID, Member member);
}
