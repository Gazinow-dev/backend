package com.gazi.gazi_renew.member.service;

import com.gazi.gazi_renew.common.config.JwtTokenProvider;
import com.gazi.gazi_renew.common.config.SecurityUtil;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.common.domain.ResponseToken;
import com.gazi.gazi_renew.common.exception.ErrorCode;
import com.gazi.gazi_renew.member.domain.dto.*;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import com.gazi.gazi_renew.route.controller.response.MyFindRoadResponse;
import com.gazi.gazi_renew.route.controller.port.MyFindRoadService;
import com.gazi.gazi_renew.notification.controller.port.NotificationService;
import com.gazi.gazi_renew.common.service.RedisUtilService;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.controller.response.MemberResponse;
import com.gazi.gazi_renew.member.controller.port.MemberService;
import com.gazi.gazi_renew.member.infrastructure.MemberEntity;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequiredArgsConstructor
@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final MyFindRoadService myFindRoadService;
    private final AuthenticationManagerBuilder managerBuilder;
    private final RedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender emailSender;
    private final RedisUtilService redisUtilService;
    private final NotificationService notificationService;

    @Override
    public Member signUp(@Valid MemberCreate memberCreate, Errors errors) {
        Member member = Member.from(memberCreate, passwordEncoder);
        validateEmail(member.getEmail());
        validateNickName(member.getNickName());

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
        memberRepository.save(member);
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
       return memberRepository.save(member);
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

        // 새로운 토큰 생성
        ResponseToken tokenInfo = jwtTokenProvider.generateMemberAndToken(authentication, member);
        // RefreshToken Redis 업데이트
        redisUtilService.setRefreshToken(authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime());

        return tokenInfo;
    }

    @Override
    public Member changeNickName(@Valid MemberNicknameValidation memberNicknameValidation, Errors errors) {
        Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
        String nickname = memberNicknameValidation.getNickname();
        if (memberRepository.existsByNickName(nickname)) {
            throw ErrorCode.throwDuplicateNicknameException();
        } else {
            member = member.changeNickname(nickname);

            return memberRepository.save(member);
        }
    }
    @Override
    @Transactional(readOnly = true)
    public boolean checkPassword(MemberCheckPassword memberCheckPassword) {
        Member member = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail())
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
        return member.isMatchesPassword(passwordEncoder, memberCheckPassword, member);
    }
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Response.Body> findPassword(Member.IsUser isUserRequest){
        try{
            String password = "";

            MemberResponse.isUser isUser = new MemberResponse.isUser();

            Optional<MemberEntity> member = memberRepository.findByEmailAndNickName(isUserRequest.getEmail(),isUserRequest.getNickname());
            if(member.isPresent()) {
                isUser.setIsUser(true);
                // 비밀번호 발급
                password = getTempPassword();
                // 비밀번호 수정
                member.get().setPassword(passwordEncoder.encode(password));
                memberRepository.save(member.get());
                // 이메일로 임시비밀번호 전송
                MimeMessage message = createMessageToPassword(isUserRequest.getEmail(), password);
                try{//예외처리
                    emailSender.send(message);
                }catch(MailException es){
                    es.printStackTrace();
                    throw new IllegalArgumentException();
                }
            }
            return response.success("임시비밀번호 발급: " +password);
        }catch (Exception e){
            return response.fail(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }


    //랜덤함수로 임시비밀번호 구문 만들기
    public String getTempPassword(){
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        char[] specialSet = new char[] {'!', '#', '$', '%', '&','~'};


        String str = "";

        // 문자 배열 길이의 값을 랜덤으로 10개를 뽑아 구문을 작성함
        int idx = 0;
        for (int i = 0; i < 8; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }

        for (int i = 0; i < 2 ; i++) {
            idx = (int) (specialSet.length * Math.random());
            str += specialSet[idx];
        }
        return str;
    }



    @Override
    public ResponseEntity<Response.Body> changePassword(@Valid Member.Password passwordDto, Errors errors) {
        try {
            MemberEntity memberEntity = memberRepository.getReferenceByEmail(SecurityUtil.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
            Member.CheckPassword checkPassword = new Member.CheckPassword();
            checkPassword.setCheckPassword(passwordDto.getCurPassword());

            if (checkPassword(checkPassword).getStatusCode().equals(HttpStatus.OK)) {
                if (passwordDto.getChangePassword().equals(passwordDto.getConfirmPassword())) {
                    memberEntity.setPassword(passwordEncoder.encode(passwordDto.getChangePassword()));
                    memberRepository.save(memberEntity);
                    return response.success("비밀번호 변경을 완료했습니다.");
                } else {
                    return response.fail("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
                }
            } else {
                return response.fail("현재 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Response.Body> deleteMember(Member.DeleteMember deleteMemberDto) {
        try{
            String email = SecurityUtil.getCurrentUserEmail();
            MemberEntity memberEntity = memberRepository.getReferenceByEmail(email).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
            memberRepository.delete(memberEntity);

            // Redis 에서 해당 Member email 로 저장된 Access Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
            if (redisTemplate.opsForValue().get("AT:" + email) != null) {
                // Refresh Token 삭제
                redisTemplate.delete("AT:" + email);
            }
            // Redis 에서 해당 Member email 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
            if (redisTemplate.opsForValue().get("RT:" + email) != null) {
                // Refresh Token 삭제
                redisTemplate.delete("RT:" + email);
            }
            return response.success("회원 탈퇴 완료.");

        }catch (Exception e){
            return response.fail(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    /* 회원가입 시, 유효성 체크 */
    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<Response.Body> validateHandling(Errors errors) {
        Map<String, String> validatorResult = new HashMap<>();

        // 유효성 검사에 실패한 필드 목록을 받음
        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }
        return response.fail(validatorResult, "유효성 검증 실패", HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<Response.Body> sendSimpleMessage(String to) throws Exception {
        Response.Body chekEmailBody = checkEmail(to).getBody();
        if(chekEmailBody.getResult().equals("fail")){
            return response.fail(chekEmailBody.getMessage(), HttpStatus.CONFLICT);
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
            redisUtilService.setDataExpire(to,keyValue,60*5L);
        }else{
            redisTemplate.delete(to);
            redisUtilService.setDataExpire(to,keyValue,60*5L);
        }

        return response.success(keyValue,"인증번호를 발송하였습니다.", HttpStatus.OK);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Response.Body> checkEmail(String email) {
        try {
            validateEmail(email);
            return response.success("회원가입이 가능한 이메일입니다.");
        } catch (IllegalStateException e) {
            return response.fail("이미 가입된 이메일입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Response.Body> checkNickName(String nickName) {
        if(memberRepository.existsByNickName(nickName)) {
            return response.fail("중복된 닉네임입니다.", HttpStatus.CONFLICT);
        } else{
            return response.success(nickName,"사용가능한 닉네임입니다.", HttpStatus.OK);
        }

    }
    /**
     * 유저 푸시 알림 활성/비활성 메서드
     * 푸시 알림 꺼지면 내가 저장한 경로 알림 비활성화
     * @param : MemberRequest.AlertAgree alertAgreeRequest
     * @return Response.Body
     */
    @Override
    public ResponseEntity<Response.Body> updatePushNotificationStatus(Member.AlertAgree alertAgreeRequest) {
        MemberEntity memberEntity = memberRepository.findByEmail(alertAgreeRequest.getEmail()).orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        memberEntity.setPushNotificationEnabled(alertAgreeRequest.isAlertAgree());
        if (!alertAgreeRequest.isAlertAgree()) {
            updateMySavedRouteNotificationStatus(alertAgreeRequest);
        }
        // 푸시알림이 켜지면 아래 알림도 다 켜져야함
        if (alertAgreeRequest.isAlertAgree()) {
            updateMySavedRouteNotificationStatus(alertAgreeRequest);
            updateRouteDetailNotificationStatus(alertAgreeRequest);
        }
        memberRepository.save(memberEntity);
        return response.success("푸시 알림 수신 설정이 저장되었습니다.");
    }
    /**
     * 내가 저장한 경로 알림 활성/비활성 메서드
     * 내가 저장한 경로 꺼지면 경로별 상세 설정 알림 비활성화
     * @param : MemberRequest.AlertAgree alertAgreeRequest
     * @return Response.Body
     */
    @Override
    public ResponseEntity<Response.Body> updateMySavedRouteNotificationStatus(Member.AlertAgree alertAgreeRequest) {
        MemberEntity memberEntity = memberRepository.findByEmail(alertAgreeRequest.getEmail()).orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        memberEntity.setMySavedRouteNotificationEnabled(alertAgreeRequest.isAlertAgree());
        if (!alertAgreeRequest.isAlertAgree()) {
            updateRouteDetailNotificationStatus(alertAgreeRequest);
        }
        memberRepository.save(memberEntity);
        return response.success("내가 저장한 경로 알림 수신 설정이 저장되었습니다.");
    }
    /**
     * 경로별 상세 설정 알림 활성/비활성 메서드
     * 경로별 상세 설정 알림 꺼지면 나의 상세 경로 알림들 모두 비활성화
     * @param : MemberRequest.AlertAgree alertAgreeRequest
     * @return Response.Body
     */
    @Override
    public ResponseEntity<Response.Body> updateRouteDetailNotificationStatus(Member.AlertAgree alertAgreeRequest) {
        MemberEntity memberEntity = memberRepository.findByEmail(alertAgreeRequest.getEmail()).orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        memberEntity.setRouteDetailNotificationEnabled(alertAgreeRequest.isAlertAgree());
        if (!alertAgreeRequest.isAlertAgree()) {
            // myFindRoadService에서 경로 데이터를 가져옴
            ResponseEntity<Response.Body> response = myFindRoadService.getRoutes();

            // Response.Body에서 데이터를 추출
            List<MyFindRoadResponse> routes = (List<MyFindRoadResponse>) response.getBody().getData();

            // 경로 리스트에서 MyPathId를 추출하여 notificationService에 전달
            for (MyFindRoadResponse route : routes) {
                // MyPathId를 넘겨서 삭제 메서드 호출
                notificationService.deleteNotificationTimes(route.getId());
                myFindRoadService.updateRouteNotification(route.getId(), false);
            }

        }
        memberRepository.save(memberEntity);
        return response.success("경로 상세 설정 알림 수신 설정이 저장되었습니다.");
    }
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Response.Body> getPushNotificationStatus(String email) {
        MemberEntity memberEntity = memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        MemberResponse.AlertAgree memberResponse = new MemberResponse.AlertAgree(memberEntity.getEmail(), memberEntity.getPushNotificationEnabled());
        return response.success(memberResponse, "", HttpStatus.OK);
    }
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Response.Body> getMySavedRouteNotificationStatus(String email) {
        MemberEntity memberEntity = memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        MemberResponse.AlertAgree memberResponse = new MemberResponse.AlertAgree(memberEntity.getEmail(), memberEntity.getMySavedRouteNotificationEnabled());
        return response.success(memberResponse, "", HttpStatus.OK);
    }
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Response.Body> getRouteDetailNotificationStatus(String email) {
        MemberEntity memberEntity = memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        MemberResponse.AlertAgree memberResponse = new MemberResponse.AlertAgree(memberEntity.getEmail(), memberEntity.getRouteDetailNotificationEnabled());
        return response.success(memberResponse, "", HttpStatus.OK);
    }
    /**
     * 소셜로그인시 푸시알림을 위해 토큰 저장할 메서드
     * @param : MemberRequest.FcmTokenRequest fcmTokenRequest
     * @return Response.Body
     */
    @Override
    public ResponseEntity<Response.Body> saveFcmToken(Member.FcmTokenRequest fcmTokenRequest) {
        MemberEntity memberEntity = memberRepository.findByEmail(fcmTokenRequest.getEmail()).orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        memberEntity.saveFcmToken(fcmTokenRequest.getFirebaseToken());
        memberRepository.save(memberEntity);
        return response.success("FireBase 토큰 저장 완료.");
    }
}
