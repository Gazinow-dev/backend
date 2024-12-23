package com.gazi.gazi_renew.member.infrastructure.jpa;

import com.gazi.gazi_renew.member.infrastructure.RecentSearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecentSearchJpaRepository extends JpaRepository<RecentSearchEntity, Long> {
    List<RecentSearchEntity> findAllByMemberIdOrderByModifiedAtDesc(Long memberId);
    Optional<RecentSearchEntity> findByMemberIdAndStationLineAndStationName(Long memberId, String stationLine, String stationName);

    Optional<RecentSearchEntity> findByIdAndMemberId(Long recentSearchID, Long memberId);
    @Modifying
    @Query("UPDATE RecentSearchEntity r SET r.modifiedAt = :modifiedAt WHERE r.id = :id")
    void updateModifiedAt(@Param("id") Long id, @Param("modifiedAt") LocalDateTime modifiedAt);

    void deleteByMemberId(Long memberId);
}
