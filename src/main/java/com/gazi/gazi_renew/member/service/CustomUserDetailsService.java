package com.gazi.gazi_renew.member.service;


import com.gazi.gazi_renew.member.infrastructure.MemberEntity;
import com.gazi.gazi_renew.member.infrastructure.jpa.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberJpaRepository.findByEmail(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));
    }

    private UserDetails createUserDetails(MemberEntity memberEntity) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(memberEntity.getRole().toString());
        return new User(String.valueOf(memberEntity.getEmail()), memberEntity.getPassword(), Collections.singleton(grantedAuthority));
    }
}
