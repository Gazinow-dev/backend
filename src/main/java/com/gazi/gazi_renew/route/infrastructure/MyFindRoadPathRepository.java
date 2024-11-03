package com.gazi.gazi_renew.route.infrastructure;

import com.gazi.gazi_renew.user.infrastructure.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MyFindRoadPathRepository extends JpaRepository<MyFindRoadPathEntity,Long> {
    List<MyFindRoadPathEntity> findAllByMemberOrderByIdDesc(MemberEntity memberEntity);
    boolean existsByNameAndMember(String roadName, MemberEntity memberEntity);
    Optional<List<MyFindRoadPathEntity>> findAllByFirstStartStationAndLastEndStationAndMemberAndTotalTime(String startStation, String lastStation, MemberEntity memberEntity, int totalTime);
    MyFindRoadPathEntity findMyFindRoadPathById(Long id);
    List<MyFindRoadPathEntity> findByMemberId(Long memberId);
}
