package com.gazi.gazi_renew.member.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberNicknameValidation {
    @Pattern(regexp = "^[A-Za-z0-9가-힣]{1,7}$", message = "7글자 까지만 허용됩니다. (ㄱ,ㄴ,ㄷ 같은형식 입력 불가능)")
    // 7글자 수정 영어 소문자, 대문자,번호, 한글(ㄱ,ㄴ,ㄷ 같은형식 입력불가능)
    private final String nickname;
    @Builder
    @JsonCreator
    public MemberNicknameValidation(String nickname) {
        this.nickname = nickname;
    }
}
