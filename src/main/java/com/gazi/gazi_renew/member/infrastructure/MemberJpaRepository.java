package com.gazi.gazi_renew.member.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickName(String nickName);
    Optional<MemberEntity> getReferenceByEmail(String email);

    Optional<MemberEntity> findByEmailAndNickName(String email, String nickname);
}
