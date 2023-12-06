package com.gazi.gazi_renew.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "SUBWAY",  indexes = {
        @Index(name = "line", columnList = "line"),
        @Index(name = "name", columnList = "name")
})
@Entity
public class Subway {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String line;
    private String name;
    private int code;
    private double lat;
    private double lng;
}