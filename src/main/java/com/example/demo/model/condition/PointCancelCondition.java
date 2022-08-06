package com.example.demo.model.condition;


import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PointCancelCondition {

    @Min(value = 1)
    @NotNull
    private Long id;

    @Ignore
    private Long memberId;
}
