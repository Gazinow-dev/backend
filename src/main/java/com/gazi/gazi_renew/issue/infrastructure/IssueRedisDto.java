package com.gazi.gazi_renew.issue.infrastructure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use= JsonTypeInfo.Id.DEDUCTION)
public class IssueRedisDto {
    private long start;
    private long end;
}