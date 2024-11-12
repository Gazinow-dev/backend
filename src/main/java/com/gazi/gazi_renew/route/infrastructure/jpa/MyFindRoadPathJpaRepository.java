package com.gazi.gazi_renew.route.infrastructure.jpa;

import com.gazi.gazi_renew.member.infrastructure.MemberEntity;
import com.gazi.gazi_renew.route.infrastructure.MyFindRoadPathEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MyFindRoadPathJpaRepository extends JpaRepository<MyFindRoadPathEntity, Long> {
    List<MyFindRoadPathEntity> findAllByMemberEntityIdOrderByIdDesc(Long memberEntityId);

    boolean existsByNameAndMemberEntity(String roadName, MemberEntity memberEntity);

    Optional<List<MyFindRoadPathEntity>> findAllByFirstStartStationAndLastEndStationAndMemberEntityAndTotalTime(String startStation, String lastStation, MemberEntity memberEntity, int totalTime);

    MyFindRoadPathEntity findMyFindRoadPathById(Long id);

    List<MyFindRoadPathEntity> findByMemberEntityId(Long memberEntityId);
}
