package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.example.demo.meta.Common.DATE_TIME_FORMAT;
import static com.example.demo.meta.Common.TIMEZONE;
import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
public class PointHistory {

    @ApiModelProperty(value = "포인트 내역 아이디")
    private Long id;

    @ApiModelProperty(value = "포인트 구분")
    private String pointActionType;

    @ApiModelProperty(value = "포인트")
    private long point;

    @ApiModelProperty(value = "처리시간")
    @JsonFormat(shape = STRING, pattern = DATE_TIME_FORMAT, timezone = TIMEZONE)
    private LocalDateTime createdAt;

}
