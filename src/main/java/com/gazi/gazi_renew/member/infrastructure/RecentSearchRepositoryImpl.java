package com.gazi.gazi_renew.member.infrastructure;

import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.domain.RecentSearch;
import com.gazi.gazi_renew.member.infrastructure.jpa.RecentSearchJpaRepository;
import com.gazi.gazi_renew.member.service.port.RecentSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RecentSearchRepositoryImpl implements RecentSearchRepository {
    private final RecentSearchJpaRepository recentSearchJpaRepository;
    @Override
    public List<RecentSearch> findAllByMemberOrderByModifiedAtDesc(Member member) {
        return recentSearchJpaRepository.findAllByMemberEntityOrderByModifiedAtDesc(MemberEntity.from(member))
                .stream().map(RecentSearchEntity::toModel).collect(Collectors.toList());
    }

    @Override
    public Optional<RecentSearch> findByMemberAndStationLineAndStationName(Member member, String stationLine, String stationName) {
        return recentSearchJpaRepository.findByMemberEntityAndStationLineAndStationName(MemberEntity.from(member), stationLine, stationName)
                .map(RecentSearchEntity::toModel);
    }

    @Override
    public Optional<RecentSearch> findByIdAndMember(Long recentSearchID, Member member) {
        return recentSearchJpaRepository.findByIdAndMemberEntity(recentSearchID, MemberEntity.from(member))
                .map(RecentSearchEntity::toModel);
    }

    @Override
    public void delete(RecentSearch recentSearch) {
        recentSearchJpaRepository.delete(RecentSearchEntity.from(recentSearch));
    }

    @Override
    public RecentSearch save(RecentSearch recentSearch) {
        return recentSearchJpaRepository.save(RecentSearchEntity.from(recentSearch)).toModel();
    }

}
