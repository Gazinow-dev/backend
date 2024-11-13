package com.gazi.gazi_renew.member.domain;

import com.gazi.gazi_renew.member.domain.dto.MemberCreate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class MemberTest {
    @Test
    void Member는_MemberCreate_객체로_생성할_수_있다() throws Exception{
        //given
        MemberCreate memberCreate = MemberCreate.builder()
                .email("mw310@naver.com")
                .password("temp")
                .nickName("minu")
                .firebaseToken("firebase")
                .build();

        //when
        Member.from(memberCreate, new Pass)

        //then

    }
}