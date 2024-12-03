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
    @Override
    public void delete(Member member) {
        memberJpaRepository.delete(MemberEntity.from(member));
    }

    @Override
    public void updateFireBaseToken(Member member) {
        memberJpaRepository.updateFireBaseToken(member.getFirebaseToken(), member.getEmail());
    }

    @Override
    public void updateNickname(Member member) {
        memberJpaRepository.updateNickname(member.getId(), member.getNickName());
    }

    @Override
    public void updatePassword(Member member) {
        memberJpaRepository.updatePassword(member.getId(), member.getPassword());
    }

    @Override
    public void updateAlertAgree(Member member) {
        memberJpaRepository.updateAlertAgree(member.getPushNotificationEnabled(), member.getMySavedRouteNotificationEnabled(),
                member.getEmail());
    }

    @Override
    public Optional<Member> findById(Long id) {
        return memberJpaRepository.findById(id).map(MemberEntity::toModel);
    }

}
