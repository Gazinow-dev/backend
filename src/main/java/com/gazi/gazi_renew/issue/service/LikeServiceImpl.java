package com.gazi.gazi_renew.issue.service;

import com.gazi.gazi_renew.common.config.SecurityUtil;
import com.gazi.gazi_renew.issue.infrastructure.IssueEntity;
import com.gazi.gazi_renew.issue.infrastructure.LikeEntity;
import com.gazi.gazi_renew.issue.controller.port.LikeService;
import com.gazi.gazi_renew.member.infrastructure.MemberEntity;
import com.gazi.gazi_renew.issue.domain.Like;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.issue.infrastructure.jpa.IssueJpaRepository;
import com.gazi.gazi_renew.issue.infrastructure.jpa.LikeJpaRepository;
import com.gazi.gazi_renew.member.infrastructure.jpa.MemberJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class LikeServiceImpl implements LikeService {

    private final IssueJpaRepository issueJpaRepository;
    private final LikeJpaRepository likeJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final Response response;
    @Override
    public ResponseEntity<Response.Body> likeIssue(Like like) {
        try{
            IssueEntity issueEntity = issueJpaRepository.findById(like.getIssueId()).orElseThrow( () -> new EntityNotFoundException("선택한 id가 없습니다."));
            MemberEntity memberEntity = memberJpaRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

            if(!likeJpaRepository.existsByIssueAndMember(issueEntity, memberEntity)){
                LikeEntity likeEntity = LikeEntity.builder()
                        .issueEntity(issueEntity)
                        .memberEntity(memberEntity)
                        .build();
                likeJpaRepository.save(likeEntity);

                return response.success(issueEntity.getId() +"번의 좋아요를 눌렀습니다.");
            }else{
                return response.fail("이미 좋아요를 누른 이슈 입니다.", HttpStatus.CONFLICT);
            }
        }catch(EntityNotFoundException e){
            return response.fail(e.getMessage(),HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return response.fail(e.getMessage(),HttpStatus.BAD_GATEWAY);
        }
    }

    @Override
    public ResponseEntity<Response.Body> deleteLikeIssue(Like dto) {
        try{
            IssueEntity issueEntity = issueJpaRepository.findById(dto.getIssueId()).orElseThrow( () -> new EntityNotFoundException("선택한 id가 없습니다."));
            MemberEntity memberEntity = memberJpaRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

            //이슈와 맴버를 찾는 로직추가
            LikeEntity likeEntity = likeJpaRepository.findByIssueAndMember(issueEntity, memberEntity).orElseThrow(
                    () -> new EntityNotFoundException("데이터를 찾지 못헀습니다.")
            );
            likeJpaRepository.delete(likeEntity);
            return response.success("데이터 삭제 성공");
        }catch (EntityNotFoundException e){
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch (Exception e){
            log.error(e.getMessage());
            return response.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
}
