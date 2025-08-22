package com.gazi.gazi_renew.member.service.port;
import com.gazi.gazi_renew.member.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickName(String nickName);
    Optional<Member> getReferenceByEmail(String email);

    Optional<Member> findByEmailAndNickName(String email, String nickname);

    Member save(Member member);

    void delete(Member member);

    void updateFireBaseToken(Member member);

    void updateNickname(Member member);

    void updatePassword(Member member);

    void updateAlertAgree(Member member);

    Optional<Member> findById(Long id);
    long count();

    List<Long> findIdsByNextDayNotificationEnabled(Boolean enabled);
}
