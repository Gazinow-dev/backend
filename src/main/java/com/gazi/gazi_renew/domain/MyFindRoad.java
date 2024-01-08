package com.gazi.gazi_renew.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "MY_FIND_LOAD")
@Entity
public class MyFindRoad extends AuditingFields{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    private String name;
    @OneToMany(mappedBy = "myFindRoad", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MyFindRoadPath> paths = new ArrayList<>();

}
