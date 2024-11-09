package com.gazi.gazi_renew.member.infrastructure.jpa;

import com.gazi.gazi_renew.member.infrastructure.MemberEntity;
import com.gazi.gazi_renew.member.infrastructure.RecentSearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecentSearchJpaRepository extends JpaRepository<RecentSearchEntity, Long> {
    List<RecentSearchEntity> findAllByMemberOrderByModifiedAtDesc(MemberEntity memberEntity);
    Optional<RecentSearchEntity> findByMemberAndStationLineAndStationName(MemberEntity memberEntity, String stationLine, String stationName);

    Optional<RecentSearchEntity> findByIdAndMember(Long recentSearchID, MemberEntity memberEntity);
}