package com.gazi.gazi_renew.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class RecentSearch extends AuditingFields{

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

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

}
