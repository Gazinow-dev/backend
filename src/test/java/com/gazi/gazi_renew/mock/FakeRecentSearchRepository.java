package com.gazi.gazi_renew.mock;

import com.gazi.gazi_renew.member.domain.RecentSearch;
import com.gazi.gazi_renew.member.service.port.RecentSearchRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class FakeRecentSearchRepository implements RecentSearchRepository {
    private final AtomicLong autoGeneratedId = new AtomicLong(21);
    private final List<RecentSearch> data = new ArrayList<>();

    @Override
    public List<RecentSearch> findAllByMemberOrderByModifiedAtDesc(Long memberId) {
        return data.stream()
                .filter(recentSearch -> recentSearch.getMemberId().equals(memberId)) // memberId 필터링
                .sorted(Comparator.comparing(RecentSearch::getModifiedAt).reversed()) // modifiedAt 내림차순 정렬
                .collect(Collectors.toList());
    }

    @Override
    public Optional<RecentSearch> findByMemberAndStationLineAndStationName(Long memberId, String stationLine, String stationName) {
        return data.stream()
                .filter(recentSearch -> recentSearch.getMemberId().equals(memberId))
                .filter(recentSearch -> recentSearch.getStationLine().equals(stationLine))
                .filter(recentSearch -> recentSearch.getStationName().equals(stationName))
                .findFirst();
    }

    @Override
    public Optional<RecentSearch> findByIdAndMember(Long recentSearchID, Long memberId) {
        return data.stream()
                .filter(recentSearch -> recentSearch.getId().equals(recentSearchID))
                .filter(recentSearch -> recentSearch.getMemberId().equals(memberId))
                .findFirst();
    }

    @Override
    public void delete(RecentSearch recentSearch) {
        data.removeIf(existing -> existing.getId().equals(recentSearch.getId()));
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        data.removeIf(recentSearch -> recentSearch.getMemberId().equals(memberId));
    }

    @Override
    public RecentSearch save(RecentSearch recentSearch) {
        if (recentSearch.getId() == null) {
            recentSearch = RecentSearch.builder()
                    .id(autoGeneratedId.getAndIncrement())
                    .stationName(recentSearch.getStationName())
                    .stationLine(recentSearch.getStationLine())
                    .memberId(recentSearch.getMemberId())
                    .modifiedAt(recentSearch.getModifiedAt())
                    .build();
        } else {
            delete(recentSearch);
        }
        data.add(recentSearch);
        return recentSearch;
    }

    @Override
    public void updateModifiedAt(Long id, LocalDateTime modifiedAt) {
        data.stream()
                .filter(recentSearch -> recentSearch.getId().equals(id))
                .findFirst()
                .ifPresent(recentSearch -> {
                    RecentSearch updatedSearch = RecentSearch.builder()
                            .id(recentSearch.getId())
                            .stationName(recentSearch.getStationName())
                            .stationLine(recentSearch.getStationLine())
                            .memberId(recentSearch.getMemberId())
                            .modifiedAt(modifiedAt) // 업데이트된 시간 설정
                            .build();
                    delete(recentSearch); // 기존 데이터 삭제
                    data.add(updatedSearch); // 새 데이터 추가
                });
    }
}
