package com.gazi.gazi_renew.route.infrastructure;

import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.infrastructure.MemberEntity;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.infrastructure.jpa.MyFindRoadPathJpaRepository;
import com.gazi.gazi_renew.route.service.port.MyFindRoadPathRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MyFindRoadPathRepositoryImpl implements MyFindRoadPathRepository {
    private final MyFindRoadPathJpaRepository myFindRoadPathJpaRepository;


    @Override
    public List<MyFindRoad> findAllByMemberOrderByIdDesc(Member member) {
        return myFindRoadPathJpaRepository.findAllByMemberOrderByIdDesc(MemberEntity.from(member)).stream()
                .map(MyFindRoadPathEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public boolean existsByNameAndMember(String roadName, Member member) {
        return myFindRoadPathJpaRepository.existsByNameAndMember(roadName, MemberEntity.from(member));
    }

    @Override
    public Optional<List<MyFindRoad>> findAllByFirstStartStationAndLastEndStationAndMemberAndTotalTime(String startStation, String lastStation, Member member, int totalTime) {
        return myFindRoadPathJpaRepository
                .findAllByFirstStartStationAndLastEndStationAndMemberAndTotalTime(startStation, lastStation, MemberEntity.from(member), totalTime)
                .map(entities -> entities.stream()
                        .map(MyFindRoadPathEntity::toModel)
                        .collect(Collectors.toList()));
    }

    @Override
    public MyFindRoad findMyFindRoadPathById(Long id) {
        return myFindRoadPathJpaRepository.findMyFindRoadPathById(id).toModel();
    }

    @Override
    public List<MyFindRoad> findByMemberId(Long memberId) {
        return myFindRoadPathJpaRepository.findByMemberId(memberId).stream()
                .map(MyFindRoadPathEntity::toModel).collect(Collectors.toList());
    }
}
