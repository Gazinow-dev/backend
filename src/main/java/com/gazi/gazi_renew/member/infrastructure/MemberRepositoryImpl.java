package com.gazi.gazi_renew.member.infrastructure;

import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.infrastructure.jpa.MemberJpaRepository;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Optional<Member> findByEmail(String email) {
        return memberJpaRepository.findByEmail(email).map(MemberEntity::toModel);
    }

    @Override
    public boolean existsByEmail(String email) {
        return memberJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickName(String nickName) {
        return memberJpaRepository.existsByNickName(nickName);
    }

    @Override
    public Optional<Member> getReferenceByEmail(String email) {
        return memberJpaRepository.getReferenceByEmail(email).map(MemberEntity::toModel);
    }

    @Override
    public Optional<Member> findByEmailAndNickName(String email, String nickname) {
        return memberJpaRepository.findByEmailAndNickName(email, nickname).map(MemberEntity::toModel);
    }

    @Override
    public Member save(Member member) {
        return memberJpaRepository.save(MemberEntity.from(member)).toModel();
    }

}
