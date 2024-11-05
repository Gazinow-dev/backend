package com.gazi.gazi_renew.member.service.port;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.infrastructure.IssueEntity;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.infrastructure.MemberEntity;

import java.util.Optional;

public interface MemberRepository {
    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickName(String nickName);
    Optional<Member> getReferenceByEmail(String email);

    Optional<Member> findByEmailAndNickName(String email, String nickname);

    Member save(Member member);
}
