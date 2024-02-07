package com.gazi.gazi_renew.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class IssueResponse {

    private Long id;
    private String title;
    private String content;
    private String date;
    private String line;
    private LocalDateTime startDate;
    private LocalDateTime expireDate;
}
