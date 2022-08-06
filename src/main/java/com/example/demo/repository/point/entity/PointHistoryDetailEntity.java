package com.example.demo.repository.point.entity;

import com.example.demo.meta.PointActionType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

import static com.example.demo.meta.PointActionType.SAVE;

@Entity
@Getter
@Setter
@Table(name = "point_history_detail")
public class PointHistoryDetailEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "point_history_detail_id")
    private Long pointHistoryDetailId;

    @Column(name = "point_action_type")
    @Enumerated(EnumType.STRING)
    private PointActionType pointActionType;

    @Column(name = "point")
    private long point;

    @Column(name = "expired_at")
    private LocalDate expiredAt;

    @Column(name = "isExpired")
    private Boolean isExpired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_history_id", referencedColumnName = "id")
    private PointHistoryEntity pointHistoryEntity;

    @PostPersist
    public void postPersist(){
        if(SAVE.equals(this.pointActionType)){
            this.pointHistoryDetailId = this.id;
        }
    }

}
