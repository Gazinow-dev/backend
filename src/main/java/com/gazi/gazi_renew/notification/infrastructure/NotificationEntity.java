package com.gazi.gazi_renew.notification.infrastructure;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gazi.gazi_renew.common.domain.AuditingFields;
import com.gazi.gazi_renew.notification.domain.Notification;
import com.gazi.gazi_renew.route.infrastructure.MyFindRoadPathEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notification")
public class NotificationEntity extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String dayOfWeek;

    @Column(nullable = false)
    private LocalTime fromTime;

    @Column(nullable = false)
    private LocalTime toTime;

    @Column(name = "my_find_road_path_id", nullable = false)
    private Long myFindRoadPathId;

    public Notification toModel() {
        return Notification.builder()
                .id(id)
                .dayOfWeek(dayOfWeek)
                .fromTime(fromTime)
                .toTime(toTime)
                .myFindRoadPathId(myFindRoadPathId)
                .build();
    }

    public static NotificationEntity from(Notification notification) {
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.dayOfWeek = notification.getDayOfWeek();
        notificationEntity.fromTime = notification.getFromTime();
        notificationEntity.toTime = notification.getToTime();
        notificationEntity.myFindRoadPathId = notification.getMyFindRoadPathId();

        return notificationEntity;
    }
}