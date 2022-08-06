package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.example.demo.meta.Common.DATE_TIME_FORMAT;
import static com.example.demo.meta.Common.TIMEZONE;
import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
public class PointHistory {

    private Long id;
    private String pointActionType;
    private long point;
    @JsonFormat(shape = STRING, pattern = DATE_TIME_FORMAT, timezone = TIMEZONE)
    private LocalDateTime createdAt;

}
