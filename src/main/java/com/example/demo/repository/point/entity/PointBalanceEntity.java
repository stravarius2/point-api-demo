package com.example.demo.repository.point.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "point_balance")
public class PointBalanceEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "total_point")
    private long totalPoint = 0L;

    @OneToMany(mappedBy = "pointBalanceEntity", cascade=CascadeType.ALL, orphanRemoval=true)
    @OrderBy("id desc")
    private List<PointHistoryEntity> pointHistoryEntities;

}
