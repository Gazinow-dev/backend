package com.gazi.gazi_renew.route.service.port;

import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.infrastructure.MyFindRoadPathEntity;

import java.util.List;
import java.util.Optional;

public interface MyFindRoadPathRepository {
    List<MyFindRoad> findAllByMemberOrderByIdDesc(Member member);

    boolean existsByNameAndMember(String roadName, Member member);
    Optional<List<MyFindRoad>> findAllByFirstStartStationAndLastEndStationAndMemberAndTotalTime(String startStation, String lastStation, Member member, int totalTime);
    MyFindRoad findMyFindRoadPathById(Long id);
    List<MyFindRoad> findByMemberId(Long memberId);
}
