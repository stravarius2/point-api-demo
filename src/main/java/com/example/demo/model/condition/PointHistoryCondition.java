package com.example.demo.model.condition;


import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

@Getter
@Setter
public class PointHistoryCondition {

    @Min(value = 1)
    private int page = 1;

    @Range(min = 1, max = 40, message = "페이지 최대 사이즈는 40입니다.")
    private int size = 20;

    @Ignore
    private Long memberId;

}
