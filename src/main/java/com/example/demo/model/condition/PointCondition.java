package com.example.demo.model.condition;


import com.example.demo.meta.PointActionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "포인트")
    private long point;

    @JsonIgnore
    private Long memberId;

    @JsonIgnore
    private PointActionType pointActionType;
}
