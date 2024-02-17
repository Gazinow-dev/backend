package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.config.SecurityUtil;
import com.gazi.gazi_renew.domain.Member;
import com.gazi.gazi_renew.domain.RecentSearch;
import com.gazi.gazi_renew.dto.RecentSearchRequest;
import com.gazi.gazi_renew.dto.RecentSearchResponse;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.repository.MemberRepository;
import com.gazi.gazi_renew.repository.RecentSearchRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecentSearchServiceImpl implements RecentSearchService {

    private final MemberRepository memberRepository;
    private final RecentSearchRepository recentSearchRepository;
    private final Response response;


    public Member isUser() {
        Member member = memberRepository.findByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(
                () -> new EntityNotFoundException("해당 회원이 존재하지 않습니다.")
        );
        return member;
    }

    @Override
    public ResponseEntity<Response.Body> recentGet() {
        try {
            Member member = isUser();
            // 최근 수정일자로 정렬
            List<RecentSearch> recentSearchList = recentSearchRepository.findAllByMemberOrderByModifiedAtDesc(member);
            List<RecentSearchResponse> recentSearchResponseList = recentSearchList.stream().map(recentSearch -> {
                RecentSearchResponse dto = RecentSearchResponse.getDto(recentSearch);
                return dto;
            }).collect(Collectors.toList());
            return response.success(recentSearchResponseList);
        }
        catch (EntityNotFoundException e){
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            return response.fail("조회 실패", HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public ResponseEntity<Response.Body> recentAdd(RecentSearchRequest dto) {
        try {
            Member member = isUser();
            RecentSearch recentSearch;

            Optional<RecentSearch> recentSearchOptional = recentSearchRepository.findByStationLineAndStationName(dto.getStationLine(),dto.getStationName());
            if(recentSearchOptional.isPresent()){
                recentSearch = recentSearchOptional.get();
                recentSearch.setModifiedAt(LocalDateTime.now());
            }else{
                recentSearch = dto.toRecentSearch(member);
            }
            validateMaxSize(member);
            recentSearchRepository.save(recentSearch);
            RecentSearchResponse recentSearchResponse = RecentSearchResponse.getDto(recentSearch);
            return response.success(recentSearchResponse,"최근검색 추가 성공",HttpStatus.CREATED);
        }
        catch (EntityNotFoundException e){
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public ResponseEntity<Response.Body> recentDelete(Long recentSearchID) {
        try {
            Member member = isUser();
            // 삭제하려고 하는 최근검색 리스트가 본인것이 맞는지 재확인
            RecentSearch recentSearch = recentSearchRepository.findByIdAndMember(recentSearchID, member).orElseThrow(
                    () -> new EntityNotFoundException("최근 검색결과를 찾을 수 없거나 본인이 검색한 결과가 아닙니다.")
            );

            recentSearchRepository.delete(recentSearch);
            return response.success("검색결과가 삭제되었습니다.");
        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    public void validateMaxSize(Member member) {
        List<RecentSearch> recentSearches = recentSearchRepository.findAllByMemberOrderByModifiedAtDesc(member);

        if (recentSearches.size() >= 10) {
            RecentSearch recentSearch = recentSearches.get(0);
            recentSearchRepository.delete(recentSearch);
            log.info("검색결과가 10개 이상이 되어 " + member.getNickName() + " 의 가장 오래된 검색값" + recentSearch.getStationName() + "을 삭제하였습니다.");
        }

    }
}
