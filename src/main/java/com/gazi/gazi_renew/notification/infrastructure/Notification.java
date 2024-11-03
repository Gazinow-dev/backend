package com.gazi.gazi_renew.notification.infrastructure;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gazi.gazi_renew.common.domain.AuditingFields;
import com.gazi.gazi_renew.route.infrastructure.MyFindRoadPathEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "notification")
@Entity
public class Notification extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String dayOfWeek;

    @Column(nullable = false)
    private LocalTime fromTime;

    @Column(nullable = false)
    private LocalTime toTime;

    @ManyToOne
    @JoinColumn(name = "my_find_road_path_id", nullable = false)
    @JsonBackReference
    private MyFindRoadPathEntity myFindRoadPathEntity;

    // 알림 업데이트 메서드
    public void updateNotification(String dayOfWeek, LocalTime fromTime, LocalTime toTime) {
        this.dayOfWeek = dayOfWeek;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }
}