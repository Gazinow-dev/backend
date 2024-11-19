package com.gazi.gazi_renew.member.domain;

import com.gazi.gazi_renew.member.domain.dto.MemberCheckPassword;
import com.gazi.gazi_renew.member.domain.dto.MemberCreate;
import com.gazi.gazi_renew.member.domain.enums.Role;
import com.gazi.gazi_renew.mock.TestPasswordEncoder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        TestPasswordEncoder passwordEncoder = new TestPasswordEncoder();
        //when
        Member member = Member.from(memberCreate, passwordEncoder);
        //then
        assertThat(member.getEmail()).isEqualTo("mw310@naver.com");
        assertThat(member.getPassword()).isEqualTo("encoded_temp");
        assertThat(member.getNickName()).isEqualTo("minu");
        assertThat(member.getFirebaseToken()).isEqualTo("firebase");
    }
    @Test
    void Member는_fireBaseToken을_저장할_수있다() throws Exception{
        //given
        Member member = Member.builder()
                .email("mw310@naver.com")
                .password("temp")
                .nickName("minu")
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(true)
                .mySavedRouteNotificationEnabled(true)
                .routeDetailNotificationEnabled(true)
                .firebaseToken("temp")
                .build();
        String firebaseToken = "saveFireBaseToken";
        //when
        member = member.saveFireBaseToken(firebaseToken);
        //then
        assertThat(member.getEmail()).isEqualTo("mw310@naver.com");
        assertThat(member.getNickName()).isEqualTo("minu");
        assertThat(member.getFirebaseToken()).isEqualTo(firebaseToken);
    }
    @Test
    void Member는_nickname을_변경할_수_있다() throws Exception{
        //given
        Member member = Member.builder()
                .email("mw310@naver.com")
                .password("temp")
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(true)
                .mySavedRouteNotificationEnabled(true)
                .routeDetailNotificationEnabled(true)
                .firebaseToken("firebaseToken")
                .build();
        //when
        member = member.changeNickname("minu");
        //then
        assertThat(member.getEmail()).isEqualTo("mw310@naver.com");
        assertThat(member.getNickName()).isEqualTo("minu");
        assertThat(member.getFirebaseToken()).isEqualTo("firebaseToken");
    }
    @Test
    void Member는_password를_변경할_수_있다() throws Exception{
        //given
        Member member = Member.builder()
                .email("mw310@naver.com")
                .password("tempPassword")
                .nickName("minu")
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(true)
                .mySavedRouteNotificationEnabled(true)
                .routeDetailNotificationEnabled(true)
                .firebaseToken("firebaseToken")
                .build();
        TestPasswordEncoder passwordEncoder = new TestPasswordEncoder();
        //when
        member = member.changePassword(passwordEncoder, "changePassword");
        //then
        assertThat(member.getEmail()).isEqualTo("mw310@naver.com");
        assertThat(member.getNickName()).isEqualTo("minu");
        assertThat(member.getFirebaseToken()).isEqualTo("firebaseToken");
        assertThat(member.getPassword()).isEqualTo("encoded_changePassword");
    }
    @Test
    void Member는_푸시알림을_끌_수_있다() throws Exception{
        //given
        Member member = Member.builder()
                .email("mw310@naver.com")
                .password("tempPassword")
                .nickName("minu")
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(true)
                .mySavedRouteNotificationEnabled(true)
                .routeDetailNotificationEnabled(true)
                .firebaseToken("firebaseToken")
                .build();
        //when
        member = member.updatePushNotificationEnabled(false);
        //then
        assertThat(member.getEmail()).isEqualTo("mw310@naver.com");
        assertThat(member.getNickName()).isEqualTo("minu");
        assertThat(member.getFirebaseToken()).isEqualTo("firebaseToken");
        assertThat(member.getPushNotificationEnabled()).isEqualTo(false);
    }
    @Test
    void Member가_푸시알림을_끄면_다른_알림도_같이_꺼진다() throws Exception{
        //given
        Member member = Member.builder()
                .email("mw310@naver.com")
                .password("tempPassword")
                .nickName("minu")
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(true)
                .mySavedRouteNotificationEnabled(true)
                .routeDetailNotificationEnabled(true)
                .firebaseToken("firebaseToken")
                .build();
        //when
        member = member.updatePushNotificationEnabled(false);
        //then
        assertThat(member.getEmail()).isEqualTo("mw310@naver.com");
        assertThat(member.getNickName()).isEqualTo("minu");
        assertThat(member.getFirebaseToken()).isEqualTo("firebaseToken");
        assertThat(member.getPushNotificationEnabled()).isEqualTo(false);
        assertThat(member.getMySavedRouteNotificationEnabled()).isEqualTo(false);
        assertThat(member.getRouteDetailNotificationEnabled()).isEqualTo(false);
    }
    @Test
    void Member가_내가_저장한_경로_알림을_끌_수_있다() throws Exception{
        //given
        Member member = Member.builder()
                .email("mw310@naver.com")
                .password("tempPassword")
                .nickName("minu")
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(true)
                .mySavedRouteNotificationEnabled(true)
                .routeDetailNotificationEnabled(true)
                .firebaseToken("firebaseToken")
                .build();
        //when
        member = member.updateMySavedRouteNotificationEnabled(false);
        //then
        assertThat(member.getEmail()).isEqualTo("mw310@naver.com");
        assertThat(member.getNickName()).isEqualTo("minu");
        assertThat(member.getFirebaseToken()).isEqualTo("firebaseToken");
        assertThat(member.getPushNotificationEnabled()).isEqualTo(true);
        assertThat(member.getMySavedRouteNotificationEnabled()).isEqualTo(false);
    }
    @Test
    void Member가_경로별_상세_설정_알림을_끌_수_있다() throws Exception{
        //given
        Member member = Member.builder()
                .email("mw310@naver.com")
                .password("tempPassword")
                .nickName("minu")
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(true)
                .mySavedRouteNotificationEnabled(true)
                .routeDetailNotificationEnabled(true)
                .firebaseToken("firebaseToken")
                .build();
        //when
        member = member.updateRouteDetailNotificationEnabled(false);
        //then
        assertThat(member.getEmail()).isEqualTo("mw310@naver.com");
        assertThat(member.getNickName()).isEqualTo("minu");
        assertThat(member.getFirebaseToken()).isEqualTo("firebaseToken");
        assertThat(member.getPushNotificationEnabled()).isEqualTo(true);
        assertThat(member.getMySavedRouteNotificationEnabled()).isEqualTo(true);
        assertThat(member.getRouteDetailNotificationEnabled()).isEqualTo(false);
    }
    @Test
    void Member는_비밀번호_일치_여부를_확인할_수_있다() throws Exception{
        //given
        MemberCreate memberCreate = MemberCreate.builder()
                .email("mw310@naver.com")
                .password("tempPassword")
                .nickName("minu")
                .firebaseToken("firebase")
                .build();
        TestPasswordEncoder passwordEncoder = new TestPasswordEncoder();
        //when
        Member member = Member.from(memberCreate, passwordEncoder);
        MemberCheckPassword password = MemberCheckPassword.builder()
                .checkPassword("tempPassword")
                .build();
        //when
        boolean matchesPassword = member.isMatchesPassword(passwordEncoder, password, member);
        //then
        assertThat(matchesPassword).isTrue();
    }
}