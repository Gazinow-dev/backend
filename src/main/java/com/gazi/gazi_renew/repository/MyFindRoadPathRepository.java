package com.gazi.gazi_renew.repository;

import com.gazi.gazi_renew.domain.Member;
import com.gazi.gazi_renew.domain.MyFindRoadPath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MyFindRoadPathRepository extends JpaRepository<MyFindRoadPath,Long> {
    List<MyFindRoadPath> findAllByMemberOrderByIdDesc(Member member);
    boolean existsByNameAndMember(String roadName, Member member);
    Optional<List<MyFindRoadPath>> findAllByFirstStartStationAndLastEndStationAndMemberAndTotalTime(String startStation, String lastStation, Member member, int totalTime);
}
