package com.gazi.gazi_renew.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.common.config.JwtTokenProvider;
import com.gazi.gazi_renew.common.controller.port.SecurityUtilService;
import com.gazi.gazi_renew.common.controller.port.RedisUtilService;
import com.gazi.gazi_renew.common.domain.ResponseToken;
import com.gazi.gazi_renew.common.exception.ErrorCode;
import com.gazi.gazi_renew.member.domain.dto.*;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import com.gazi.gazi_renew.notification.domain.Notification;
import com.gazi.gazi_renew.notification.service.port.NotificationRepository;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.controller.port.MemberService;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.service.port.MyFindRoadPathRepository;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.*;


@Slf4j
@Service
@Builder
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final MyFindRoadPathRepository myFindRoadPathRepository;
    private final AuthenticationManagerBuilder managerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender emailSender;
    private final RedisUtilService redisUtilService;
    private final SecurityUtilService securityUtilService;
    private final NotificationRepository notificationRepository;

    @Override
    public Member signUp(@Valid MemberCreate memberCreate, Errors errors) {
        Member member = Member.from(memberCreate, passwordEncoder);
        validateEmail(member.getEmail());
        validateNickName(member.getNickName());
        //oto
        memberRepository.save(member);
        return member;
    }
    @Transactional(readOnly = true)
    public void validateEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }
    }
    @Transactional(readOnly = true)
    public void validateNickName(String nickName) {
        if (memberRepository.existsByNickName(nickName)) {
            throw new IllegalStateException("중복된 닉네임입니다.");
        }
    }

    @Override
    public ResponseToken login(@Valid MemberLogin memberLogin) {
        Member member = memberRepository.findByEmail(memberLogin.getEmail()).orElseThrow(
                () -> new EntityNotFoundException("존재하지 않는 회원의 아이디입니다.")
        );
        // memberLogin email, password 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = memberLogin.usernamePasswordAuthenticationToken();
        // login 시 받은 firebase token 저장
        member = member.saveFireBaseToken(memberLogin.getFirebaseToken());
        memberRepository.updateFireBaseToken(member);
        // 실제 검증 (사용자 비밀번호 체크)
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = managerBuilder.getObject().authenticate(authenticationToken); // 인증 정보를 기반으로 JWT 토큰 생성
        ResponseToken responseToken = jwtTokenProvider.generateMemberAndToken(authentication, member);

        redisUtilService.setRefreshToken(authentication.getName(), responseToken.getRefreshToken(),
                responseToken.getRefreshTokenExpirationTime());
        return responseToken;
    }


    @Override
    public Member logout(MemberLogout memberLogout) {
        // Access Token 에서 Member email 을 가져옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(memberLogout.getAccessToken());

        // Redis 에서 해당 Member email 로 저장된 Access Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
        String accessTokenKey = "AT:" + authentication.getName();
        if (redisUtilService.getData(accessTokenKey) != null) {
            redisUtilService.deleteToken(accessTokenKey);
        }
        // Redis 에서 해당 Member email 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
        String refreshTokenKey = "RT:" + authentication.getName();
        if (redisUtilService.getData(refreshTokenKey) != null) {
            redisUtilService.deleteToken(refreshTokenKey);
        }
        // Access Token 유효시간 가지고 와서 BlackList 로 저장하기
        Long expiration = jwtTokenProvider.getExpiration(memberLogout.getAccessToken());
        redisUtilService.addToBlacklist(memberLogout.getAccessToken(), expiration);
        // firebaseToken 삭제
        Member member = memberRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new EntityNotFoundException("회원을 찾을 수 없습니다.")
        );
        return member;
    }
    @Override
    public ResponseToken reissue(MemberReissue memberReissue) {
        // Refresh Token 검증
        if (!jwtTokenProvider.validateToken(memberReissue.getRefreshToken())) {
            throw ErrorCode.InvalidRefreshToken();
        }
        // Access Token 에서 Member email 가져옴
        Authentication authentication = jwtTokenProvider.getAuthentication(memberReissue.getAccessToken());

        log.info("유저 email: " + authentication.getName());
        log.info("엑세스 토큰 만료까지 남은시간(ms): " + jwtTokenProvider.getExpiration(memberReissue.getAccessToken()));
        log.info("리프레쉬 토큰 만료까지 남은시간(ms): " + jwtTokenProvider.getExpiration(memberReissue.getRefreshToken()));

        // Redis 에서 Member email 을 기반으로 저장된 Refresh Token 을 가져옴
        String refreshToken = redisUtilService.getData("RT:" + authentication.getName());
        log.info("Redis에서 찾은 refreshToken: " + refreshToken);

        // 로그아웃 상태로 Refresh Token 이 존재하지 않는 경우 처리
        if (ObjectUtils.isEmpty(refreshToken)) {
            throw new IllegalStateException("Redis 에 RefreshToken 이 존재하지 않습니다. 엑세스 토큰으로 찾은 유저 이메일: " + authentication.getName());
        }

        // Redis에 저장된 Refresh Token과 요청된 Refresh Token 비교
        if (!refreshToken.equals(memberReissue.getRefreshToken())) {
            throw ErrorCode.throwInvalidRefreshTokenMissMatch();
        }

        Member member = memberRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        member = member.saveFireBaseToken(memberReissue.getFirebaseToken());

        memberRepository.updateFireBaseToken(member);

        // 새로운 토큰 생성
        ResponseToken tokenInfo = jwtTokenProvider.generateMemberAndToken(authentication, member);
        // RefreshToken Redis 업데이트
        redisUtilService.setRefreshToken(authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime());

        return tokenInfo;
    }

    @Override
    public Member changeNickName(@Valid MemberNicknameValidation memberNicknameValidation, Errors errors) {
        Member member = memberRepository.getReferenceByEmail(securityUtilService.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
        String nickname = memberNicknameValidation.getNickname();
        if (memberRepository.existsByNickName(nickname)) {
            throw ErrorCode.throwDuplicateNicknameException();
        } else {
            member = member.changeNickname(nickname);

            memberRepository.updateNickname(member);
            return member;
        }
    }
    @Override
    @Transactional(readOnly = true)
    public boolean checkPassword(MemberCheckPassword memberCheckPassword) {
        Member member = memberRepository.getReferenceByEmail(securityUtilService.getCurrentUserEmail())
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

        return member.isMatchesPassword(passwordEncoder, memberCheckPassword, member);
    }
    @Override
    public String findPassword(IsMember isMember){
        try{
            Member member = memberRepository.findByEmailAndNickName(isMember.getEmail(), isMember.getNickname())
                    .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
            // 비밀번호 발급
            String tempPassword = member.getTempPassword();

            member = member.changePassword(passwordEncoder, tempPassword);
            memberRepository.updatePassword(member);
            // 이메일로 임시비밀번호 전송
            MimeMessage message = createMessageToPassword(isMember.getEmail(), tempPassword);
            try{
                emailSender.send(message);
            }catch(MailException es){
                es.printStackTrace();
                throw new IllegalArgumentException();
            }
            return tempPassword;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Member changePassword(@Valid MemberChangePassword memberChangePassword, Errors errors) {
        Member member = memberRepository.getReferenceByEmail(securityUtilService.getCurrentUserEmail())
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
        MemberCheckPassword memberCheckPassword = MemberCheckPassword.fromMemberChangePassword(memberChangePassword);

        if (checkPassword(memberCheckPassword)) {
            if (memberChangePassword.getChangePassword().equals(memberChangePassword.getConfirmPassword())) {
                member = member.changePassword(passwordEncoder, memberChangePassword.getChangePassword());

                memberRepository.updatePassword(member);
                return member;
            } else {
                throw ErrorCode.throwInvalidPassword();
            }
        } else {
            throw ErrorCode.throwInvalidCurPassword();
        }
    }

    @Override
    public Member deleteMember(MemberDelete memberDelete) {
        String email = securityUtilService.getCurrentUserEmail();
        Member member = memberRepository.getReferenceByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
        memberRepository.delete(member);
        // Redis 에서 해당 Member email 로 저장된 Access Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
        if (redisUtilService.getData("AT:" + email) != null) {
            // Refresh Token 삭제
            redisUtilService.deleteToken("AT:" + email);
        }
        // Redis 에서 해당 Member email 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
        if (redisUtilService.getData("RT:" + email) != null) {
            // Refresh Token 삭제
            redisUtilService.deleteToken("RT:" + email);
        }
        return member;
    }

    /* 회원가입 시, 유효성 체크 */
    @Transactional(readOnly = true)
    @Override
    public Map<String, String> validateHandling(Errors errors) {
        Map<String, String> validatorResult = new HashMap<>();

        // 유효성 검사에 실패한 필드 목록을 받음
        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }
        return validatorResult;
    }
    @Override
    @Transactional(readOnly = true)
    public boolean checkEmail(String email) {
        try {
            validateEmail(email);
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkNickName(String nickName) {
        if (!memberRepository.existsByNickName(nickName)) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 유저 푸시 알림 활성/비활성 메서드
     * 푸시 알림 꺼지면 내가 저장한 경로 알림 비활성화
     * @param : MemberRequest.AlertAgree alertAgreeRequest
     * @return Response.Body
     */
    @Override
    public Member updatePushNotificationStatus(MemberAlertAgree memberAlertAgree) throws JsonProcessingException {
        Member member = memberRepository.findByEmail(memberAlertAgree.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        member = member.updatePushNotificationEnabled(memberAlertAgree.isAlertAgree());
        //비활성화 시, 하위 알림 모두 비활성화
        if (!memberAlertAgree.isAlertAgree()) {
            resetRouteNotifications(member);
        }
        //활성화할 땐, 초기 러시아워 알림 설정도 같이 활성화
        if (memberAlertAgree.isAlertAgree()) {
            initializeRushHourNotificationSettings(member);
        }
        memberRepository.updateAlertAgree(member);
        return member;
    }
    /**
     * 내가 저장한 경로 알림 활성/비활성 메서드
     * 내가 저장한 경로 활성화하면, 월~금 러시아워시간 자동 개별 알림 등록
     * 내가 저장한 경로 꺼지면 경로별 개별 알림 비활성화
     * @param : MemberRequest.AlertAgree alertAgreeRequest
     * @return Response.Body
     */
    @Override
    public Member updateMySavedRouteNotificationStatus(MemberAlertAgree memberAlertAgree) throws JsonProcessingException {
        Member member = memberRepository.findByEmail(memberAlertAgree.getEmail()).orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        boolean alertAgree = memberAlertAgree.isAlertAgree();

        member = member.updateMySavedRouteNotificationEnabled(alertAgree);
        //비활성화 시, 하위 알림 모두 비활성화
        if (!memberAlertAgree.isAlertAgree()) {
            resetRouteNotifications(member);
        }
        //활성화할 땐, 초기 러시아워 알림 설정도 같이 활성화
        if (memberAlertAgree.isAlertAgree()) {
            initializeRushHourNotificationSettings(member);
        }
        memberRepository.updateAlertAgree(member);
        return member;
    }
    @Override
    @Transactional(readOnly = true)
    public Member getPushNotificationStatus(String email) {
         return memberRepository.findByEmail(email)
                 .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    }
    @Override
    @Transactional(readOnly = true)
    public Member getMySavedRouteNotificationStatus(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    }
    /**
     * 소셜로그인시 푸시알림을 위해 토큰 저장할 메서드
     * @param : MemberRequest.FcmTokenRequest fcmTokenRequest
     * @return Response.Body
     */
    @Override
    public Member saveFcmToken(MemberFcmToken memberFcmToken) {
        Member member = memberRepository.findByEmail(memberFcmToken.getEmail()).orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        member = member.saveFireBaseToken(memberFcmToken.getFirebaseToken());
        memberRepository.updateFireBaseToken(member);

        return member;
    }

    private MimeMessage createMessageToPassword(String to, String password)throws Exception{
        MimeMessage  message = emailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to);//보내는 대상
        message.setSubject("가는길 지금 이메일 인증");//제목

        String msgg="";
        msgg+= "<div style='margin:20px; color=#000000'>";
        msgg+= "<h1> 임시 비밀번호 발급 </h1>";
        msgg+= "<p> 안녕하세요 가는길지금 Gazi 입니다.";
        msgg+= "<br>";
        msgg+= "아래 임시비밀번호를 통해 로그인 해주세요.</p>";
        msgg+= "비밀번호 변경을 통해 원하시는 비밀번호로 변경 바랍니다.";
        msgg+= "<br>";
        msgg+= "<p>감사합니다.<p>";
        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid white; font-family:verdana; background-color: #F2EBFF';>";
        msgg+= "<div style='font-size:300%; color: #8446E7'>";
        msgg+= password;
        msgg+= "</div>";
        msgg+= "</div>";
        msgg+= "<hr>";
        msgg+= "<p><span style='color:#323232; font-weight:bold'>가는길지금에 가입하신 적이 없다면, 이 메일을 무시하세요.</span> <br> ";
        msgg+= "<span style='color:#9D9D9D'>본 메일은 발신 전용으로 문의에 대한 회신이 되지 않습니다. 궁금한 사항은 gazinowcs@gmail.com로 문의 부탁드립니다.</span></p>";


        msgg+= "</div>";
        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress("gazinowcs@gmail.com","gazi"));//보내는 사람

        return message;
    }

    private MimeMessage createMessage(String to, String keyValue)throws Exception{
        MimeMessage  message = emailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to);//보내는 대상
        message.setSubject("가는길 지금 이메일 인증");//제목

        String msgg="";
        msgg+= "<div style='margin:20px; color=#000000'>";
        msgg+= "<h1> 이메일 인증 </h1>";
        msgg+= "<p> 안녕하세요 가는길지금 Gazi 입니다.";
        msgg+= "<br>";
        msgg+= "아래 번호를 인증번호 입력란에 입력 후 회원가입을 완료해주세요.</p>";
        msgg+= "<br>";
        msgg+= "<p>감사합니다.<p>";
        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid white; font-family:verdana; background-color: #F2EBFF';>";
        msgg+= "<div style='font-size:300%; color: #8446E7'>";
        msgg+= keyValue;
        msgg+= "</div>";
        msgg+= "</div>";
        msgg+= "<hr>";
        msgg+= "<p><span style='color:#323232; font-weight:bold'>가는길지금에 가입하신 적이 없다면, 이 메일을 무시하세요.</span> <br> ";
        msgg+= "<span style='color:#9D9D9D'>본 메일은 발신 전용으로 문의에 대한 회신이 되지 않습니다. 궁금한 사항은 gazinowcs@gmail.com로 문의 부탁드립니다.</span></p>";


        msgg+= "</div>";
        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress("gazinowcs@gmail.com","gazi"));//보내는 사람

        return message;
    }
    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 4; i++) { // 인증코드 8자리
            key.append(rnd.nextInt(10));
        }
        return key.toString();
    }
    @Override
    public String sendSimpleMessage(String to) throws Exception {
        boolean checked = checkEmail(to);
        if(!checked){
            throw ErrorCode.throwDuplicateEmailException();
        }
        String keyValue = createKey();
        MimeMessage message = createMessage(to,keyValue);
        try{//예외처리
            emailSender.send(message);
        }catch(MailException es){
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        if(redisUtilService.getData(to) == null){
            //유효시간 5분
            redisUtilService.setDataExpire(to, keyValue, 60 * 5L);
        }else{

            redisUtilService.deleteToken(to);
            redisUtilService.setDataExpire(to,keyValue,60*5L);
        }
        return keyValue;
    }
    private void initializeRushHourNotificationSettings(Member member) throws JsonProcessingException {
        List<MyFindRoad> myFindRoadList = myFindRoadPathRepository.findByMemberId(member.getId());
        for (MyFindRoad myFindRoad : myFindRoadList) {
            List<Notification> notificationList = Notification.initNotification(myFindRoad.getId());

            notificationRepository.saveAll(notificationList);
            redisUtilService.saveNotificationTimes(notificationList, myFindRoad.getId());

            myFindRoad = myFindRoad.updateNotification(true);
            myFindRoadPathRepository.updateNotification(myFindRoad);
        }
    }
    private void resetRouteNotifications(Member member) {
        List<MyFindRoad> myFindRoadList = myFindRoadPathRepository.findAllByMemberOrderByIdDesc(member);
        // 경로 리스트에서 MyPathId를 추출하여 notificationService에 전달
        for (MyFindRoad myFindRoad : myFindRoadList) {
            // MyPathId를 넘겨서 삭제 메서드 호출
            notificationRepository.deleteByMyFindRoad(myFindRoad);
            myFindRoad = myFindRoad.updateNotification(false);

            myFindRoadPathRepository.updateNotification(myFindRoad);
            redisUtilService.deleteNotification(myFindRoad.getId().toString());
        }
    }

}
