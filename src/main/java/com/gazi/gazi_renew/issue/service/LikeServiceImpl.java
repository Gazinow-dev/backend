package com.gazi.gazi_renew.issue.service;

import com.gazi.gazi_renew.common.controller.port.SecurityUtilService;
import com.gazi.gazi_renew.common.exception.ErrorCode;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.controller.port.LikeService;
import com.gazi.gazi_renew.issue.service.port.IssueRepository;
import com.gazi.gazi_renew.issue.service.port.LikeRepository;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.issue.domain.Like;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class LikeServiceImpl implements LikeService {

    private final IssueRepository issueRepository;
    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final SecurityUtilService securityUtilService;
    @Override
    public Long likeIssue(Like like) {
        Issue issue = issueRepository.findById(like.getIssue().getId())
                .orElseThrow(() -> new EntityNotFoundException("선택한 id가 없습니다."));
        Member member = memberRepository.getReferenceByEmail(securityUtilService.getCurrentUserEmail())
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

        if(!likeRepository.existsByIssueAndMember(issue, member)){
            likeRepository.save(like);
            return issue.getId();
        }else{
            throw ErrorCode.throwDuplicateLikeException();
        }

    }

    @Override
    public void deleteLikeIssue(Like likeRequest) {
        Issue issue = issueRepository.findById(likeRequest.getIssue().getId())
                .orElseThrow(() -> new EntityNotFoundException("선택한 id가 없습니다."));
        Member member = memberRepository.getReferenceByEmail(securityUtilService.getCurrentUserEmail())
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

        //이슈와 맴버를 찾는 로직추가
        Like like = likeRepository.findByIssueAndMember(issue, member).orElseThrow(
                () -> new EntityNotFoundException("데이터를 찾지 못헀습니다.")
        );
        likeRepository.delete(like);
    }
}
