package com.gazi.gazi_renew.user.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecentSearchRepository extends JpaRepository<RecentSearchEntity, Long> {
    List<RecentSearchEntity> findAllByMemberOrderByModifiedAtDesc(MemberEntity memberEntity);
    Optional<RecentSearchEntity> findByMemberAndStationLineAndStationName(MemberEntity memberEntity, String stationLine, String stationName);

    Optional<RecentSearchEntity> findByIdAndMember(Long recentSearchID, MemberEntity memberEntity);
}
