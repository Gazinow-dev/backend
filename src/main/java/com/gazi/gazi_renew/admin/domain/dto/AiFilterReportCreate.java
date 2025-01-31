package com.gazi.gazi_renew.admin.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AiFilterReportCreate {
    private final String issueContent;
    private final boolean aiFiltered;
    private final boolean registered;
    private final String failureReason;
    @Builder
    public AiFilterReportCreate(String issueContent, boolean aiFiltered, boolean registered, String failureReason) {
        this.issueContent = issueContent;
        this.aiFiltered = aiFiltered;
        this.registered = registered;
        this.failureReason = failureReason;
    }
}
