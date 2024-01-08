package com.gazi.gazi_renew.domain;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "MY_TRANSFER")
@Entity
public class MyTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String transferName;
    private String transferLine;
    @ManyToOne
    @JoinColumn(name = "my_find_load_id", nullable = false)
    private MyFindRoad myFindLoad;
}
