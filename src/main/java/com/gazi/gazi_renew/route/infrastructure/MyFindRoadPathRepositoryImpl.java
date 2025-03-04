package com.gazi.gazi_renew.route.infrastructure;

import com.gazi.gazi_renew.member.domain.Member;
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
        return myFindRoadPathJpaRepository.findAllByMemberIdOrderByIdDesc(member.getId()).stream()
                .map(MyFindRoadPathEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public boolean existsByNameAndMember(String roadName, Member member) {
        return myFindRoadPathJpaRepository.existsByNameAndMemberId(roadName, member.getId());
    }

    @Override
    public Optional<List<MyFindRoad>> findAllByFirstStartStationAndLastEndStationAndMemberAndTotalTime(String startStation, String lastStation, Member member, int totalTime) {
        return myFindRoadPathJpaRepository
                .findAllByFirstStartStationAndLastEndStationAndMemberIdAndTotalTime(startStation, lastStation, member.getId(), totalTime)
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

    @Override
    public Optional<MyFindRoad> findById(Long id) {
        return myFindRoadPathJpaRepository.findById(id).map(MyFindRoadPathEntity::toModel);
    }

    @Override
    public MyFindRoad save(MyFindRoad myFindRoad) {
        return myFindRoadPathJpaRepository.save(MyFindRoadPathEntity.from(myFindRoad)).toModel();
    }

    @Override
    public boolean existsById(Long id) {
        return myFindRoadPathJpaRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        myFindRoadPathJpaRepository.deleteById(id);
    }

    @Override
    public void updateNotification(MyFindRoad myFindRoad) {
        myFindRoadPathJpaRepository.updateNotification(myFindRoad.getId(), myFindRoad.getNotification());
    }

    @Override
    public List<MyFindRoad> findByFirstStartStationAndLastEndStationAndMember(String firstStation, String lastEndStation, Member member) {
        return myFindRoadPathJpaRepository.findByFirstStartStationAndLastEndStationAndMemberId(firstStation, lastEndStation, member.getId())
                .stream().map(MyFindRoadPathEntity::toModel).collect(Collectors.toList());

    }

    @Override
    public int countEnabledNotificationByMemberId(Long memberId) {
        return myFindRoadPathJpaRepository.countByMemberIdAndNotificationTrue(memberId);
    }

}
