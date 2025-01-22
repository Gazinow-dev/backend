package com.gazi.gazi_renew.issue.domain.dto;

import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.station.domain.Line;
import com.gazi.gazi_renew.station.domain.Station;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class IssueStationDetail {
    private final Issue issue;
    private final List<Station> stationList;
    private final List<Line> lineList;
    private final boolean isLike;
    private final int commentCount;
    public final boolean isCommentRestricted;
    @Builder
    public IssueStationDetail(Issue issue, List<Station> stationList, List<Line> lineList, boolean isLike, int commentCount, boolean isCommentRestricted) {
        this.issue = issue;
        this.stationList = stationList;
        this.lineList = lineList;
        this.isLike = isLike;
        this.commentCount = commentCount;
        this.isCommentRestricted = isCommentRestricted;
    }

    public static IssueStationDetail from(Issue issue, List<Station> stationList, List<Line> lineList, boolean isLike, int commentCount) {
        return IssueStationDetail.builder()
                .issue(issue)
                .stationList(stationList)
                .lineList(lineList)
                .isLike(isLike)
                .commentCount(commentCount)
                .build();
    }

    public IssueStationDetail restrictedWriteComment(boolean memberRestricted) {
        return IssueStationDetail.builder()
                .issue(issue)
                .stationList(stationList)
                .lineList(lineList)
                .isLike(isLike)
                .commentCount(commentCount)
                .isCommentRestricted(memberRestricted)
                .build();
    }
}
