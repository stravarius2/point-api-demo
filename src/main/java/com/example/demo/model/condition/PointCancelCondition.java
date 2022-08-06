package com.example.demo.model.condition;


import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "포인트 내역 아이디")
    private Long id;

    @Ignore
    private Long memberId;
}
