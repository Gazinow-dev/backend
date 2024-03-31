package com.gazi.gazi_renew.repository;

import com.gazi.gazi_renew.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.ClassUtils;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickName(String nickName);
    Optional<Member> getReferenceByEmail(String email);

    Optional<Member> findByEmailAndNickName(String email, String nickname);
}
