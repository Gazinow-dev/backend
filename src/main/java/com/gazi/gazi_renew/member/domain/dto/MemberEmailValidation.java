package com.gazi.gazi_renew.member.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberEmailValidation {
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9.%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$", message = "올바른 이메일 형식이 아닙니다.")
    // 7글자 수정 영어 소문자, 대문자,번호, 한글(ㄱ,ㄴ,ㄷ 같은형식 입력불가능)
    private final String email;
    @Builder
    @JsonCreator
    public MemberEmailValidation(String email) {
        this.email = email;
    }
}
