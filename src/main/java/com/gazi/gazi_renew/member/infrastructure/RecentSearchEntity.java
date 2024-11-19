package com.gazi.gazi_renew.member.infrastructure;

import com.gazi.gazi_renew.common.domain.AuditingFields;
import com.gazi.gazi_renew.member.domain.RecentSearch;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recent_search")
@Entity
public class RecentSearchEntity extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 역이름
    @Column(name = "StationName", length = 255)
    private String stationName;
    // 호선
    @Column(name = "Line", length = 50)
    private String stationLine;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime searchTime; // 생성일시

    @Column(name = "member_id", nullable = false)
    private Long memberId;
    public RecentSearch toModel() {
        return RecentSearch.builder()
                .id(id)
                .stationName(stationName)
                .stationLine(stationLine)
                .memberId(memberId)
                .modifiedAt(getModifiedAt())
                .build();
    }
    public static RecentSearchEntity from(RecentSearch recentSearch) {
        RecentSearchEntity recentSearchEntity = new RecentSearchEntity();
        recentSearchEntity.stationName = recentSearch.getStationName();
        recentSearchEntity.stationLine = recentSearch.getStationLine();
        recentSearchEntity.memberId = recentSearch.getMemberId();

        return recentSearchEntity;
    }
}
