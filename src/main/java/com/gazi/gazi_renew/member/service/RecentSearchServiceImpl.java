package com.gazi.gazi_renew.member.service;

import com.gazi.gazi_renew.common.config.SecurityUtil;
import com.gazi.gazi_renew.member.controller.port.RecentSearchService;
import com.gazi.gazi_renew.member.infrastructure.MemberEntity;
import com.gazi.gazi_renew.member.infrastructure.RecentSearchEntity;
import com.gazi.gazi_renew.member.domain.RecentSearch;
import com.gazi.gazi_renew.member.controller.response.RecentSearchResponse;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.member.infrastructure.jpa.MemberJpaRepository;
import com.gazi.gazi_renew.member.infrastructure.jpa.RecentSearchJpaRepository;
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

    private final MemberJpaRepository memberJpaRepository;
    private final RecentSearchJpaRepository recentSearchJpaRepository;
    private final Response response;


    public MemberEntity isUser() {
        MemberEntity memberEntity = memberJpaRepository.findByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(
                () -> new EntityNotFoundException("해당 회원이 존재하지 않습니다.")
        );
        return memberEntity;
    }

    @Override
    public ResponseEntity<Response.Body> recentGet() {
        try {
            MemberEntity memberEntity = isUser();
            // 최근 수정일자로 정렬
            List<RecentSearchEntity> recentSearchEntityList = recentSearchJpaRepository.findAllByMemberOrderByModifiedAtDesc(memberEntity);
            List<RecentSearchResponse> recentSearchResponseList = recentSearchEntityList.stream().map(recentSearch -> {
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
    public ResponseEntity<Response.Body> recentAdd(RecentSearch dto) {
        try {
            MemberEntity memberEntity = isUser();
            RecentSearchEntity recentSearchEntity;

            Optional<RecentSearchEntity> recentSearchOptional = recentSearchJpaRepository.findByMemberAndStationLineAndStationName(memberEntity, dto.getStationLine(),dto.getStationName());
            if(recentSearchOptional.isPresent()){
                recentSearchEntity = recentSearchOptional.get();
                recentSearchEntity.setModifiedAt(LocalDateTime.now());
            }else{
                recentSearchEntity = dto.toRecentSearch(memberEntity);
            }
            validateMaxSize(memberEntity);
            recentSearchJpaRepository.save(recentSearchEntity);
            RecentSearchResponse recentSearchResponse = RecentSearchResponse.getDto(recentSearchEntity);
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
            MemberEntity memberEntity = isUser();
            // 삭제하려고 하는 최근검색 리스트가 본인것이 맞는지 재확인
            RecentSearchEntity recentSearchEntity = recentSearchJpaRepository.findByIdAndMember(recentSearchID, memberEntity).orElseThrow(
                    () -> new EntityNotFoundException("최근 검색결과를 찾을 수 없거나 본인이 검색한 결과가 아닙니다.")
            );

            recentSearchJpaRepository.delete(recentSearchEntity);
            return response.success("검색결과가 삭제되었습니다.");
        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    public void validateMaxSize(MemberEntity memberEntity) {
        List<RecentSearchEntity> recentSearchEntities = recentSearchJpaRepository.findAllByMemberOrderByModifiedAtDesc(memberEntity);

        if (recentSearchEntities.size() >= 10) {
            RecentSearchEntity recentSearchEntity = recentSearchEntities.get(0);
            recentSearchJpaRepository.delete(recentSearchEntity);
            log.info("검색결과가 10개 이상이 되어 " + memberEntity.getNickName() + " 의 가장 오래된 검색값" + recentSearchEntity.getStationName() + "을 삭제하였습니다.");
        }

    }
}
