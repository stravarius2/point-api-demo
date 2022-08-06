package com.example.demo.repository.point.entity;

import com.example.demo.meta.PointActionType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "point_history")
@ToString
public class PointHistoryEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "point_action_type")
    @Enumerated(EnumType.STRING)
    private PointActionType pointActionType;

    @Column(name = "point")
    private long point;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_balance_id", referencedColumnName = "id")
    private PointBalanceEntity pointBalanceEntity;

    @OneToMany(mappedBy = "pointHistoryEntity", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<PointHistoryDetailEntity> pointHistoryDetailEntities;
}
