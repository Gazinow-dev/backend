package com.gazi.gazi_renew.repository;

import com.gazi.gazi_renew.domain.Member;
import com.gazi.gazi_renew.domain.MyFindRoadPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MyFindRoadPathRepository extends JpaRepository<MyFindRoadPath,Long> {
    List<MyFindRoadPath> findAllByMemberOrderByIdDesc(Member member);
    boolean existsByNameAndMember(String roadName, Member member);
    Optional<List<MyFindRoadPath>> findAllByFirstStartStationAndLastEndStationAndMemberAndTotalTime(String startStation, String lastStation, Member member, int totalTime);
    MyFindRoadPath findMyFindRoadPathById(Long id);
}
