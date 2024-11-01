package com.gazi.gazi_renew.user.controller.response;


import com.gazi.gazi_renew.user.infrastructure.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class MemberResponse {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SignUp {
        @NotBlank
        private String email;
        @NotBlank
        private String nickName;

        public SignUp(Member member) {
            this.email = member.getEmail();
            this.nickName = member.getNickName();
        }

    }

    @Getter
    @Setter
    public static class isUser{
        private Boolean isUser;
    }

    @Getter
    @Setter
    public static class Login {
        @NotBlank(message = "Email는 필수 입력 값입니다.")
        private String email;

        @NotBlank(message = "Password는 필수 입력 값입니다.")
        private String password;

        public UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken() {
            return new UsernamePasswordAuthenticationToken(email, password);
        }
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

        public Logout(String accessToken, String refreshToken) {
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
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "올바른 이메일 형식이 아닙니다.")
        private String email;
    }

    @Setter
    @Getter
    public static class AlertAgree {
        private String email;
        private boolean alertAgree;

        public AlertAgree(String email, boolean alertAgree) {
            this.email = email;
            this.alertAgree = alertAgree;
        }
    }

}
