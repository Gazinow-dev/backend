package com.gazi.gazi_renew.member.service;

import com.gazi.gazi_renew.common.controller.port.SecurityUtilService;
import com.gazi.gazi_renew.common.service.port.ClockHolder;
import com.gazi.gazi_renew.member.controller.port.RecentSearchService;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.domain.RecentSearch;
import com.gazi.gazi_renew.member.domain.dto.RecentSearchCreate;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import com.gazi.gazi_renew.member.service.port.RecentSearchRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecentSearchServiceImpl implements RecentSearchService {

    private final MemberRepository memberRepository;
    private final RecentSearchRepository recentSearchRepository;
    private final ClockHolder clockHolder;
    private final SecurityUtilService securityUtilService;
    @Override
    public List<RecentSearch> getRecentSearch() {
        Member member = isUser();
        // 최근 수정일자로 정렬
        List<RecentSearch> recentSearchList = recentSearchRepository.findAllByMemberOrderByModifiedAtDesc(member);
        return recentSearchList;
    }

    @Override
    public RecentSearch addRecentSearch(RecentSearchCreate recentSearchCreate) {
        Member member = isUser();
        RecentSearch recentSearch = RecentSearch.from(recentSearchCreate, member);

        Optional<RecentSearch> optionalRecentSearch = recentSearchRepository.findByMemberAndStationLineAndStationName(member,
                recentSearch.getStationLine(), recentSearch.getStationName());
        if(optionalRecentSearch.isPresent()){
            optionalRecentSearch.get().updateModifiedAt(clockHolder);
        }
        validateMaxSizeRecentSearch(member);
        return recentSearchRepository.save(recentSearch);
    }

    @Override
    public void recentDelete(Long recentSearchID) {
        Member member = isUser();
        // 삭제하려고 하는 최근검색 리스트가 본인것이 맞는지 재확인
        RecentSearch recentSearch = recentSearchRepository.findByIdAndMember(recentSearchID, member).orElseThrow(
                () -> new EntityNotFoundException("최근 검색결과를 찾을 수 없거나 본인이 검색한 결과가 아닙니다.")
        );

        recentSearchRepository.delete(recentSearch);
    }

    private void validateMaxSizeRecentSearch(Member member) {
        List<RecentSearch> recentSearchList = recentSearchRepository.findAllByMemberOrderByModifiedAtDesc(member);

        if (recentSearchList.size() >= 10) {
            RecentSearch recentSearch = recentSearchList.get(0);
            recentSearchRepository.delete(recentSearch);
            log.info("검색결과가 10개 이상이 되어 " + member.getNickName() + " 의 가장 오래된 검색값" + recentSearch.getStationName() + "을 삭제하였습니다.");
        }
    }
    private Member isUser() {
        return memberRepository.findByEmail(securityUtilService.getCurrentUserEmail()).orElseThrow(
                () -> new EntityNotFoundException("해당 회원이 존재하지 않습니다.")
        );
    }

}
