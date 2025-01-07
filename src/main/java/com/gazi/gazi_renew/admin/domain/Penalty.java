package com.gazi.gazi_renew.admin.domain;

import com.gazi.gazi_renew.common.service.port.ClockHolder;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Penalty {
    private final Long penaltyId;
    private final Long memberId;
    private final LocalDateTime startDate;
    private LocalDateTime expireDate; // 수정 가능한 필드로 변경

    @Builder
    public Penalty(Long penaltyId, Long memberId, LocalDateTime startDate, LocalDateTime expireDate) {
        this.penaltyId = penaltyId;
        this.memberId = memberId;
        this.startDate = startDate;
        this.expireDate = expireDate;
    }

    // 기간 연장 메서드
    public void extendPenalty(int days, ClockHolder clockHolder) {
        if (expireDate.isBefore(clockHolder.now())) {
            // 만료된 경우, 현재 날짜 기준으로 새로 시작
            this.expireDate = clockHolder.now().plusDays(days);
        } else {
            // 아직 만료되지 않은 경우, 기존 만료일 기준으로 연장
            this.expireDate = this.expireDate.plusDays(days);
        }
    }
}