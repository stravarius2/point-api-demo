package com.example.demo.repository.point.entity;

import javax.persistence.PostLoad;

public class DetailIdListener {

    @PostLoad
    public void updatePointHistoryDetailId(PointHistoryDetailEntity entity) {
        entity.setPointHistoryDetailId(entity.getId());
    }

}
