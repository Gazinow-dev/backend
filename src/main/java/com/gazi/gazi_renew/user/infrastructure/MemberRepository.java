package com.gazi.gazi_renew.user.infrastructure;

import com.gazi.gazi_renew.user.infrastructure.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickName(String nickName);
    Optional<Member> getReferenceByEmail(String email);

    Optional<Member> findByEmailAndNickName(String email, String nickname);
}
