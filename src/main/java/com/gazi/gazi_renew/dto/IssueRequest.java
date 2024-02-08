package com.gazi.gazi_renew.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class IssueRequest {

    private String title;
    private String content;
    private String date;
    private String line;
    private LocalDateTime startDate;
    private LocalDateTime expireDate;
    private String secretCode;
}
