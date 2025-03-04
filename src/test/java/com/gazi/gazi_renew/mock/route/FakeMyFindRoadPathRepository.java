package com.gazi.gazi_renew.mock.route;

import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.service.port.MyFindRoadPathRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FakeMyFindRoadPathRepository implements MyFindRoadPathRepository {
    private final AtomicLong autoGeneratedId = new AtomicLong(0);
    private final List<MyFindRoad> data = new ArrayList<>();
    @Override
    public List<MyFindRoad> findAllByMemberOrderByIdDesc(Member member) {
        return data.stream()
                .filter(myFindRoad -> myFindRoad.getMemberId().equals(member.getId()))
                .sorted(Comparator.comparing(MyFindRoad::getId).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByNameAndMember(String roadName, Member member) {
        return data.stream().anyMatch(myFindRoad ->
                myFindRoad.getRoadName().equals(roadName) &&
                        myFindRoad.getMemberId().equals(member.getId()));
    }

    @Override
    public Optional<List<MyFindRoad>> findAllByFirstStartStationAndLastEndStationAndMemberAndTotalTime(String startStation, String lastStation, Member member, int totalTime) {
        List<MyFindRoad> myFindRoadList = data.stream()
                .filter(myFindRoad -> myFindRoad.getFirstStartStation().equals(startStation))
                .filter(myFindRoad -> myFindRoad.getLastEndStation().equals(lastStation))
                .filter(myFindRoad -> myFindRoad.getMemberId().equals(member))
                .filter(myFindRoad -> myFindRoad.getTotalTime() == totalTime)
                .collect(Collectors.toList());

        return myFindRoadList.isEmpty() ? Optional.empty() : Optional.of(myFindRoadList);
    }

    @Override
    public MyFindRoad findMyFindRoadPathById(Long id) {
        return data.stream()
                .filter(myFindRoad -> myFindRoad.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("해당 MyFindRoadPath가 없습니다."));
    }

    @Override
    public List<MyFindRoad> findByMemberId(Long memberId) {
        return data.stream()
                .filter(myFindRoad -> myFindRoad.getMemberId().equals(memberId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MyFindRoad> findById(Long id) {
        return data.stream()
                .filter(myFindRoad -> myFindRoad.getId().equals(id))
                .findFirst();
    }

    @Override
    public MyFindRoad save(MyFindRoad myFindRoad) {
        if (myFindRoad.getId() == null || myFindRoad.getId() == 0) {
            MyFindRoad createMyFindRoad = MyFindRoad.builder()
                    .id(autoGeneratedId.incrementAndGet())
                    .roadName(myFindRoad.getRoadName())
                    .totalTime(myFindRoad.getTotalTime())
                    .stationTransitCount(myFindRoad.getStationTransitCount())
                    .firstStartStation(myFindRoad.getFirstStartStation())
                    .lastEndStation(myFindRoad.getLastEndStation())
                    .memberId(myFindRoad.getMemberId())
                    .subPaths(myFindRoad.getSubPaths())
                    .notification(myFindRoad.getNotification())
                    .build();
            data.add(createMyFindRoad);
            return createMyFindRoad;
        } else {
            data.removeIf(item -> Objects.equals(item.getId(), myFindRoad.getId()));
            data.add(myFindRoad);
            return myFindRoad;
        }
    }

    @Override
    public boolean existsById(Long id) {
        return data.stream().anyMatch(myFindRoad ->
                myFindRoad.getId().equals(id));
    }

    @Override
    public void deleteById(Long id) {
        data.removeIf(myFindRoad -> myFindRoad.getId().equals(id));
    }

    @Override
    public void updateNotification(MyFindRoad myFindRoad) {
        data.removeIf(existingMyFindRoad -> existingMyFindRoad.getId().equals(myFindRoad.getId()));

        MyFindRoad updatedMyFindRoad = MyFindRoad.builder()
                .id(myFindRoad.getId())
                .roadName(myFindRoad.getRoadName())
                .totalTime(myFindRoad.getTotalTime())
                .stationTransitCount(myFindRoad.getStationTransitCount())
                .firstStartStation(myFindRoad.getFirstStartStation())
                .lastEndStation(myFindRoad.getLastEndStation())
                .memberId(myFindRoad.getMemberId())
                .subPaths(myFindRoad.getSubPaths())
                .notification(myFindRoad.getNotification())
                .build();

        data.add(updatedMyFindRoad);
    }

    @Override
    public List<MyFindRoad> findByFirstStartStationAndLastEndStationAndMember(String firstStation, String lastEndStation, Member member) {
        return data.stream()
                .filter(myFindRoad -> myFindRoad.getFirstStartStation().equals(firstStation))
                .filter(myFindRoad -> myFindRoad.getLastEndStation().equals(lastEndStation))
                .filter(myFindRoad -> myFindRoad.getMemberId().equals(member.getId()))
                .collect(Collectors.toList());
    }
    @Override
    public int countEnabledNotificationByMemberId(Long memberId) {
        return (int) data.stream()
                .filter(myFindRoad -> myFindRoad.getMemberId().equals(memberId))
                .filter(MyFindRoad::getNotification)
                .count(); // 리스트로 수집하지 않고 직접 카운트
    }
}
