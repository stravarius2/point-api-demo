package com.example.demo.model.condition;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

@Getter
@Setter
public class PointHistoryCondition {

    @Min(value = 1)
    @ApiModelProperty(value = "페이지 번호 1부터 시작")
    private int page = 1;

    @Range(min = 1, max = 40, message = "페이지 최대 사이즈는 40입니다.")
    @ApiModelProperty(value = "페이지당 표시 1 ~ 40")
    private int size = 20;

    @JsonIgnore
    private Long memberId;

}
