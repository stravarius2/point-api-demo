package com.example.demo.model.condition;


import com.example.demo.meta.PointActionType;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class PointCondition {

    @Min(value = 1)
    @NotNull
    private long point;

    @Ignore
    private Long memberId;

    @Ignore
    private PointActionType pointActionType;
}
