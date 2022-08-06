package com.example.demo.service;

import com.example.demo.model.PointBalance;
import com.example.demo.model.PointHistory;
import com.example.demo.model.condition.PointCancelCondition;
import com.example.demo.model.condition.PointCondition;
import com.example.demo.model.condition.PointHistoryCondition;
import com.example.demo.provider.PointProvider;
import com.example.demo.repository.point.entity.PointBalanceEntity;
import com.example.demo.repository.point.entity.PointHistoryEntity;
import com.example.demo.utils.PointMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.meta.PointActionType.USE;


@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointProvider pointProvider;
    private final PointMapper pointMapper;

    public PointBalance getTotalPoint(Long memberId) {
        return pointMapper.toModel(pointProvider.getTotalPoint(memberId));
    }

    public Page<PointHistory> getPointHistory(PointHistoryCondition pointHistoryCondition) {
        Page<PointHistoryEntity> pointHistoryEntities = pointProvider.getPointHistory(
                pointHistoryCondition.getMemberId(), PageRequest.of(pointHistoryCondition.getPage() - 1, pointHistoryCondition.getSize())
        );

        List<PointHistory> list = pointHistoryEntities.getContent().stream().map(pointMapper::toModel).collect(Collectors.toList());
        return new PageImpl<>(list, pointHistoryEntities.getPageable(), pointHistoryEntities.getTotalElements());
    }

    public PointBalance setPoint(PointCondition pointCondition){
        long point = Math.abs(pointCondition.getPoint());
        if(USE.equals(pointCondition.getPointActionType())){
            point *= -1;
        }
        pointCondition.setPoint(point);
        return pointMapper.toModel(pointProvider.setPoint(pointCondition));
    }

    public PointBalance cancelPoint(PointCancelCondition pointCancelCondition){
        return pointMapper.toModel(pointProvider.cancelPoint(pointCancelCondition));
    }

}
