package com.gazi.gazi_renew.dto;


import com.gazi.gazi_renew.domain.Member;
import com.gazi.gazi_renew.domain.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MemberRequest {

    @Getter
    @Setter
    public static class Password{
        @NotBlank
        private String curPassword;
        @NotBlank
        private String changePassword;
        @NotBlank
        private String confirmPassword;
    }
    @Getter
    @Setter
    public static class SignUp {
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9.%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$" , message = "이메일 형식이 맞지 않습니다.")
        private String email;
        @NotBlank
        // 최소 8자리 이상 숫자, 특수문자가 1개 이상 포함
        @Pattern(regexp = "^(?=.*?[0-9])(?=.*?[~#?!@$ %^&*-]).{8,}$", message = "최소 8자리 이상 숫자, 특수문자가 1개 이상 포함되어야 합니다.")
        private String password;
        @NotBlank
        private String nickName;

        public Member toMember(PasswordEncoder passwordEncoder) {
            return Member.builder()
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .nickName(nickName)
                    .role(Role.ROLE_USER)
                    .isAgree(true)
                    .build();
        }
    }

    @Getter
    @Setter
    public static class Login {
        @NotBlank(message = "Email는 필수 입력 값입니다.")
        private String email;

        @NotBlank(message = "Password는 필수 입력 값입니다.")
        private String password;

        private String firebaseToken;

        public UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken() {
            return new UsernamePasswordAuthenticationToken(email, password);
        }
    }

    @Getter
    @Setter
    public static class IsUser{
        String email;
        String nickname;
    }

    @Getter
    @Setter
    public static class CheckPassword {
        private String checkPassword;
    }

    @Getter
    @Setter
    public static class Reissue {
        private String accessToken;
        private String refreshToken;

        public Reissue(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }

    @Getter
    @Setter
    public static class Logout {
        private String accessToken;
        private String refreshToken;

        public Logout (String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }

    @Getter
    @Setter
    public static class NickName {
        @Pattern(regexp = "^[A-Za-z0-9가-힣]{1,7}$", message = "7글자 까지만 허용됩니다. (ㄱ,ㄴ,ㄷ 같은형식 입력 불가능)")
        // 7글자 수정 영어 소문자, 대문자,번호, 한글(ㄱ,ㄴ,ㄷ 같은형식 입력불가능)
        private String nickName;
    }

    @Getter
    public static class Email {
        @NotBlank(message = "아이디는 필수 입력 값입니다.")
        @Pattern(regexp = "^[a-zA-Z0-9.%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$", message = "올바른 이메일 형식이 아닙니다.")
        private String email;
    }

    @Getter
    public static class DeleteMember {
    }

    @Getter
    @Setter
    public static class AlertAgree {
        private String email;
        private boolean alertAgree;
    }

    @Getter
    public static class GetAlertAgree {
        private String email;
    }
}
