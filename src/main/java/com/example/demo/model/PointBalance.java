package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointBalance {

    @ApiModelProperty(value = "총 사용가능 포인트")
    private long totalPoint;

    @JsonIgnore
    private Long memberId;

}
