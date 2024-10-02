package com.gazi.gazi_renew.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private MyFindRoadPath myFindRoadPath;
}