package com.gazi.gazi_renew.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use= JsonTypeInfo.Id.DEDUCTION)
public class IssueRedisDto {
    private long start;
    private long end;

    public IssueRedisDto(long start, long end) {
        this.start = start;
        this.end = end;
    }
}