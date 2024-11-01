package com.gazi.gazi_renew.issue.service;

import com.gazi.gazi_renew.common.config.SecurityUtil;
import com.gazi.gazi_renew.issue.infrastructure.Issue;
import com.gazi.gazi_renew.issue.infrastructure.Like;
import com.gazi.gazi_renew.issue.controller.port.LikeService;
import com.gazi.gazi_renew.user.infrastructure.Member;
import com.gazi.gazi_renew.issue.domain.LikeRequest;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.issue.infrastructure.IssueRepository;
import com.gazi.gazi_renew.issue.infrastructure.LikeRepository;
import com.gazi.gazi_renew.user.infrastructure.MemberRepository;
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

    private final IssueRepository issueRepository;
    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final Response response;
    @Override
    public ResponseEntity<Response.Body> likeIssue(LikeRequest dto) {
        try{
            Issue issue = issueRepository.findById(dto.getIssueId()).orElseThrow( () -> new EntityNotFoundException("선택한 id가 없습니다."));
            Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

            if(!likeRepository.existsByIssueAndMember(issue,member)){
                Like like = Like.builder()
                        .issue(issue)
                        .member(member)
                        .build();
                likeRepository.save(like);

                return response.success(issue.getId() +"번의 좋아요를 눌렀습니다.");
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
    public ResponseEntity<Response.Body> deleteLikeIssue(LikeRequest dto) {
        try{
            Issue issue = issueRepository.findById(dto.getIssueId()).orElseThrow( () -> new EntityNotFoundException("선택한 id가 없습니다."));
            Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

            //이슈와 맴버를 찾는 로직추가
            Like like = likeRepository.findByIssueAndMember(issue, member).orElseThrow(
                    () -> new EntityNotFoundException("데이터를 찾지 못헀습니다.")
            );
            likeRepository.delete(like);
            return response.success("데이터 삭제 성공");
        }catch (EntityNotFoundException e){
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch (Exception e){
            log.error(e.getMessage());
            return response.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
}
