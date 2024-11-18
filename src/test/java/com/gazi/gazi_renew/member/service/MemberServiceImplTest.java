package com.gazi.gazi_renew.member.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.common.domain.ResponseToken;
import com.gazi.gazi_renew.common.exception.CustomException;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.domain.dto.*;
import com.gazi.gazi_renew.member.domain.enums.Role;
import com.gazi.gazi_renew.mock.*;
import com.gazi.gazi_renew.notification.domain.Notification;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.domain.MyFindRoadStation;
import com.gazi.gazi_renew.route.domain.MyFindRoadSubPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberServiceImplTest {
    private MemberServiceImpl memberServiceImpl;

    private FakeMemberRepository fakeMemberRepository;
    private Errors errors;
    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Mock
    private Authentication authentication;

    @Mock
    private AuthenticationManager authenticationManager;
    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);

        fakeMemberRepository = new FakeMemberRepository();
        FakeNotificationRepository fakeNotificationRepository = new FakeNotificationRepository();
        FakeMyFindRoadPathRepository fakeMyFindRoadPathRepository = new FakeMyFindRoadPathRepository();
        errors = mock(Errors.class);

        ObjectMapper mapper = new ObjectMapper();
        TestPasswordEncoder testPasswordEncoder = new TestPasswordEncoder();
        String secretKey = "youcantrevealthesecretkey123401230004077896574";
        FakeJwtTokenProvider fakeJwtTokenProvider = new FakeJwtTokenProvider(secretKey);
        FakeRedisUtilServiceImpl fakeRedisUtilService = new FakeRedisUtilServiceImpl(mapper);
        FakeMailSender fakeMailSender = new FakeMailSender();
        FakeSecurityUtil fakeSecurityUtil = new FakeSecurityUtil();

        this.memberServiceImpl = MemberServiceImpl.builder()
                .memberRepository(fakeMemberRepository)
                .jwtTokenProvider(fakeJwtTokenProvider)
                .myFindRoadPathRepository(fakeMyFindRoadPathRepository)
                .passwordEncoder(testPasswordEncoder)
                .managerBuilder(authenticationManagerBuilder)
                .emailSender(fakeMailSender)
                .redisUtilService(fakeRedisUtilService)
                .notificationRepository(fakeNotificationRepository)
                .securityUtilService(fakeSecurityUtil)
                .build();

        Member member1 = Member.builder()
                .email("mw310@naver.com")
                .password("encoded_tempPassword")
                .nickName("minu")
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(false)
                .mySavedRouteNotificationEnabled(false)
                .routeDetailNotificationEnabled(false)
                .firebaseToken("firebaseToken")
                .build();

        Member member2 = Member.builder()
                .email("gazi@nater.com")
                .password("encoded_gaziPassword2")
                .nickName("gazi")
                .role(Role.ROLE_USER)
                .pushNotificationEnabled(true)
                .mySavedRouteNotificationEnabled(true)
                .routeDetailNotificationEnabled(true)
                .firebaseToken("firebaseToken")
                .build();
        fakeMemberRepository.save(member1);
        fakeMemberRepository.save(member2);
        fakeSecurityUtil.addEmail("mw310@naver.com");

        MyFindRoadSubPath subPath = MyFindRoadSubPath.builder()
                .trafficType(1)
                .distance(1200)
                .sectionTime(2)
                .stationCount(1)
                .way("삼각지")
                .door("null")
                .name("수도권 6호선")
                .stationCode(6)
                .stations(Arrays.asList(
                        MyFindRoadStation.builder().index(0).stationName("효창공원앞").build(),
                        MyFindRoadStation.builder().index(1).stationName("삼각지").build()
                ))
                .build();

        MyFindRoad myFindRoad = MyFindRoad.builder()
                .id(552L)
                .roadName("민우테스트")
                .totalTime(6)
                .lastEndStation("삼각지")
                .subPaths(Collections.singletonList(subPath))
                .notification(false)
                .memberId(member1.getId())
                .build();
        fakeMyFindRoadPathRepository.save(myFindRoad);

        Notification notification = Notification.builder()
                .id(1L)
                .dayOfWeek("월")
                .fromTime(LocalTime.parse("19:20:00"))
                .toTime(LocalTime.parse("20:20:00"))
                .myFindRoadPathId(myFindRoad.getId())
                .build();
        fakeNotificationRepository.saveAll(Arrays.asList(notification));
    }

    @Test
    void signUp은_멤버를_생성해준다() throws Exception{
        // given
        MemberCreate memberCreate = MemberCreate.builder()
                .email("newMinu@naver.com")
                .password("temp")
                .nickName("newuser")
                .firebaseToken("newFirebaseToken")
                .build();

        // when
        Member createdMember = memberServiceImpl.signUp(memberCreate, errors);

        // then
        assertThat(createdMember).isNotNull();
        assertThat(memberCreate.getEmail()).isEqualTo(createdMember.getEmail());
        assertThat("encoded_temp").isEqualTo(createdMember.getPassword());
        assertThat(memberCreate.getNickName()).isEqualTo(createdMember.getNickName());

        assertThat(fakeMemberRepository.findByEmail("newMinu@naver.com").isPresent()).isTrue();
    }

    @Test
    void signUp_중복된_이메일이_있으면_예외가_발생한다() {
        // given
        MemberCreate memberCreate = MemberCreate.builder()
                .email("mw310@naver.com")  // 이미 존재하는 이메일
                .password("temp")
                .nickName("newuser")
                .firebaseToken("newFirebaseToken")
                .build();

        // when
        assertThrows(IllegalStateException.class, () -> {
            memberServiceImpl.signUp(memberCreate, errors);
        });
    }

    @Test
    void signUp_중복된_닉네임이_있으면_예외가_발생한다() {
        // given
        MemberCreate memberCreate = MemberCreate.builder()
                .email("uniqueuser@example.com")
                .password("temp")
                .nickName("minu")  // 이미 존재하는 닉네임
                .firebaseToken("newFirebaseToken")
                .build();

        // when
        assertThrows(IllegalStateException.class, () -> {
            memberServiceImpl.signUp(memberCreate, errors);
        });
    }
    @Test
    void validateEmail은_중복된_이메일이_있으면_예외가_발생한다() throws Exception{
        //given
        // 가입된 이메일
        String email = "mw310@naver.com";
        //when
        assertThrows(IllegalStateException.class, () -> {
            memberServiceImpl.validateEmail(email);
        });
    }
    @Test
    void validateNickname은_중복된_닉네임이_있으면_예외가_발생한다() throws Exception{
        //given
        // 가입된 이메일
        String nickname = "minu";
        //when
        assertThrows(IllegalStateException.class, () -> {
            memberServiceImpl.validateNickName(nickname);
        });
    }
    @Test
    void login_정상적인_로그인_시_토큰이_생성된다() throws Exception {
        // given
        MemberLogin memberLogin = MemberLogin.builder()
                .email("mw310@naver.com")
                .password("tempPassword")
                .firebaseToken("firebaseToken")
                .build();

        // AuthenticationManagerBuilder와 관련된 Mock 설정
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                memberLogin.getEmail(), memberLogin.getPassword());

        when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
        when(authenticationManager.authenticate(authenticationToken)).thenReturn(authentication);
        when(authentication.getName()).thenReturn(memberLogin.getEmail());

        // when
        ResponseToken responseToken = memberServiceImpl.login(memberLogin);

        // then
        assertThat(responseToken).isNotNull();
        assertThat(responseToken.getEmail()).isEqualTo("mw310@naver.com");
        assertThat(responseToken.getAccessToken()).isNotEmpty();
    }
    @Test
    void login_로그인_시_파이어베이스_토큰이_업데이트_된다() throws Exception {
        // given
        MemberLogin memberLogin = MemberLogin.builder()
                .email("mw310@naver.com")
                .password("tempPassword")
                .firebaseToken("newFirebaseToken")
                .build();

        // AuthenticationManagerBuilder와 관련된 Mock 설정
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                memberLogin.getEmail(), memberLogin.getPassword());

        when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
        when(authenticationManager.authenticate(authenticationToken)).thenReturn(authentication);
        when(authentication.getName()).thenReturn(memberLogin.getEmail());

        // when
        ResponseToken responseToken = memberServiceImpl.login(memberLogin);
        // then
        assertThat(responseToken.getFirebaseToken()).isEqualTo("newFirebaseToken");
    }
    @Test
    void reissue를_통해_자동로그인을_할_수_있다() throws Exception{
        //given
        MemberReissue memberReissue = MemberReissue.builder()
                .accessToken("fakeAccessToken")
                .refreshToken("mw310@naver.com")
                .firebaseToken("firebaseToken2")
                .build();
        //when
        ResponseToken responseToken = memberServiceImpl.reissue(memberReissue);
        //then
        assertThat(responseToken.getAccessToken()).isEqualTo("fakeAccessToken");
        assertThat(responseToken.getRefreshToken()).isEqualTo("mw310@naver.com");
        assertThat(responseToken.getFirebaseToken()).isEqualTo("firebaseToken2");
    }
    @Test
    void reissue를_통해_FCM토큰도_업데이트도_할_수_있다() throws Exception{
        //given
        MemberReissue memberReissue = MemberReissue.builder()
                .accessToken("fakeAccessToken")
                .refreshToken("mw310@naver.com")
                .firebaseToken("updateFirebaseToken")
                .build();
        //when
        ResponseToken responseToken = memberServiceImpl.reissue(memberReissue);
        //then
        assertThat(responseToken.getFirebaseToken()).isEqualTo("updateFirebaseToken");
    }
    @Test
    void changeNickName을_통해_멤버는_닉네임을_변경할_수_있다() throws Exception{
        //given
        MemberNicknameValidation memberNicknameValidation = MemberNicknameValidation.builder()
                .nickname("new minu")
                .build();
        //when
        Member member = memberServiceImpl.changeNickName(memberNicknameValidation, errors);
        //then
        assertThat(member.getNickName()).isEqualTo("new minu");
    }
    @Test
    void changeNickName은_중복된_닉네임을_확인하면_에러를_뱉는다() throws Exception{
        //given
        MemberNicknameValidation memberNicknameValidation = MemberNicknameValidation.builder()
                .nickname("minu")
                .build();
        //when
        assertThrows(CustomException.class, () -> {
                    memberServiceImpl.changeNickName(memberNicknameValidation, errors);
                });
    }
    @Test
    void checkPassword을_통해_멤버는_비밀번호_일치_여부를_확인할_수_있다() throws Exception{
        //given
        MemberCheckPassword memberCheckPassword = MemberCheckPassword.builder()
                .checkPassword("tempPassword")
                .build();
        //when
        boolean checked = memberServiceImpl.checkPassword(memberCheckPassword);
        //then
        assertThat(checked).isTrue();
    }
    @Test
    void findPassword을_통해_멤버는_비밀번호를_찾을_수_있다() throws Exception{
        //given
        IsMember isMember = IsMember.builder()
                .email("mw310@naver.com")
                .nickname("minu")
                .build();
        //when
        String password = memberServiceImpl.findPassword(isMember);
        //then
        assertThat(password).isNotEqualTo("tempPassword");
    }
    @Test
    void changePassword를_통해_멤버는_비밀번호_변경할_수_있다() throws Exception{
        //given
        MemberChangePassword memberChangePassword = MemberChangePassword.builder()
                .curPassword("tempPassword")
                .changePassword("changePassword")
                .confirmPassword("changePassword")
                .build();
        //when
        Member member = memberServiceImpl.changePassword(memberChangePassword, errors);
        //then
        assertThat(member.getPassword()).isEqualTo("encoded_changePassword");
    }
    @Test
    void changePassword는_입력_비밀번호와_확인_비밀번호가_다르면_에러를_던진다() throws Exception{
        //given
        MemberChangePassword memberChangePassword = MemberChangePassword.builder()
                .curPassword("tempPassword")
                .changePassword("changePassword")
                //확인 비번이 달라요
                .confirmPassword("differentPassword")
                .build();
        //when
        assertThrows(CustomException.class, () -> {
            memberServiceImpl.changePassword(memberChangePassword, errors);
        });
    }
    @Test
    void updatePushNotificationStatus는_멤버의_푸시_알림을_활성화할_수_있다() throws Exception{
        //given
        MemberAlertAgree memberAlertAgree = MemberAlertAgree.builder()
                .email("mw310@naver.com")
                .alertAgree(true)
                .build();
        //when
        Member member = memberServiceImpl.updatePushNotificationStatus(memberAlertAgree);
        //then
        assertThat(member.getPushNotificationEnabled()).isTrue();
    }
    @Test
    void updatePushNotificationStatus는_멤버의_푸시_알림을_활성하면_하위_알림도_활성화() throws Exception{
        //given
        MemberAlertAgree memberAlertAgree = MemberAlertAgree.builder()
                .email("mw310@naver.com")
                .alertAgree(true)
                .build();
        //when
        Member member = memberServiceImpl.updatePushNotificationStatus(memberAlertAgree);
        //then
        assertThat(member.getMySavedRouteNotificationEnabled()).isTrue();
        assertThat(member.getRouteDetailNotificationEnabled()).isTrue();
    }
    @Test
    void updateMySavedRouteNotificationStatus는_멤버의_내가_저장한_경로_알림을_활성화할_수_있다() throws Exception{
        //given
        MemberAlertAgree memberAlertAgree = MemberAlertAgree.builder()
                .email("mw310@naver.com")
                .alertAgree(true)
                .build();
        //when
        Member member = memberServiceImpl.updateMySavedRouteNotificationStatus(memberAlertAgree);
        //then
        assertThat(member.getMySavedRouteNotificationEnabled()).isTrue();
    }
    @Test
    void updateMySavedRouteNotificationStatus는_멤버의_내가_저장한_경로_알림을_비활성하면_하위_알림도_비활성화() throws Exception{
        //given
        MemberAlertAgree memberAlertAgree = MemberAlertAgree.builder()
                //member2
                .email("gazi@nater.com")
                .alertAgree(false)
                .build();
        //when
        Member member = memberServiceImpl.updateMySavedRouteNotificationStatus(memberAlertAgree);
        //then
        assertThat(member.getRouteDetailNotificationEnabled()).isFalse();
    }
    @Test
    void updateRouteDetailNotificationStatus는_멤버의_경로별_상세_설정_알림을_활성화할_수_있다() throws Exception{
        //given
        MemberAlertAgree memberAlertAgree = MemberAlertAgree.builder()
                .email("mw310@naver.com")
                .alertAgree(true)
                .build();
        //when
        Member member = memberServiceImpl.updateRouteDetailNotificationStatus(memberAlertAgree);
        //then
        assertThat(member.getRouteDetailNotificationEnabled()).isTrue();
    }
    @Test
    void getPushNotificationStatus는_멤버의_푸시_알림을_조회할_수_있다() throws Exception{
        //given
        String email = "mw310@naver.com";
        //when
        Member member = memberServiceImpl.getPushNotificationStatus(email);
        //then
        assertThat(member.getPushNotificationEnabled()).isFalse();
    }
    @Test
    void getMySavedRouteNotificationStatus는_멤버의_내가_저장한_경로_알림을_조회할_수_있다() throws Exception{
        //given
        String email = "mw310@naver.com";
        //when
        Member member = memberServiceImpl.getMySavedRouteNotificationStatus(email);
        //then
        assertThat(member.getMySavedRouteNotificationEnabled()).isFalse();
    }
    @Test
    void getRouteDetailNotificationStatus는_멤버의_경로별_상세_설정_알림을_조회할_수_있다() throws Exception{
        //given
        String email = "mw310@naver.com";
        //when
        Member member = memberServiceImpl.getRouteDetailNotificationStatus(email);
        //then
        assertThat(member.getRouteDetailNotificationEnabled()).isFalse();
    }
    @Test
    void saveFcmTokens는_멤버의_FCM토큰을_저장할_수_있다() throws Exception{
        //given
        MemberFcmToken memberFcmToken = MemberFcmToken.builder()
                .email("mw310@naver.com")
                .firebaseToken("minu_firebaseToken")
                .build();
        //when
        Member member = memberServiceImpl.saveFcmToken(memberFcmToken);
        //then
        assertThat(member.getFirebaseToken()).isEqualTo("minu_firebaseToken");

    }
}
