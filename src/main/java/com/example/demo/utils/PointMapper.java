package com.example.demo.utils;

import com.example.demo.meta.PointActionType;
import com.example.demo.model.PointBalance;
import com.example.demo.model.PointHistory;
import com.example.demo.repository.point.entity.PointBalanceEntity;
import com.example.demo.repository.point.entity.PointHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {PointActionType.class})
public interface PointMapper{

    PointBalance toModel(PointBalanceEntity entity);

    @Mapping(target="pointActionType", expression = "java(PointActionType.from(entity.getPointActionType()))")
    PointHistory toModel(PointHistoryEntity entity);

}
