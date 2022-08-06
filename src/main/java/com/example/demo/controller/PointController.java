package com.example.demo.controller;

import com.example.demo.model.PointBalance;
import com.example.demo.model.PointHistory;
import com.example.demo.model.condition.PointCancelCondition;
import com.example.demo.model.condition.PointCondition;
import com.example.demo.model.condition.PointHistoryCondition;
import com.example.demo.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.example.demo.meta.PointActionType.SAVE;
import static com.example.demo.meta.PointActionType.USE;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping("/point")
    public PointBalance getPoint(@RequestHeader("memberId") Long memberId) {
        return pointService.getTotalPoint(memberId);
    }

    @GetMapping("/point/history")
    public Page<PointHistory> getPointHistory(@RequestHeader("memberId") Long memberId, @ModelAttribute @Valid PointHistoryCondition condition) {
        condition.setMemberId(memberId);
        return pointService.getPointHistory(condition);
    }

    @PostMapping("/point/save")
    public PointBalance savePoint(@RequestHeader("memberId") Long memberId, @RequestBody PointCondition condition) {
        condition.setMemberId(memberId);
        condition.setPointActionType(SAVE);
        return pointService.setPoint(condition);
    }

    @PostMapping("/point/use")
    public PointBalance usePoint(@RequestHeader("memberId") Long memberId, @RequestBody PointCondition condition) {
        condition.setMemberId(memberId);
        condition.setPointActionType(USE);
        return pointService.setPoint(condition);
    }

    @DeleteMapping("/point/cancel")
    public PointBalance cancelPoint(@RequestHeader("memberId") Long memberId, @ModelAttribute @Valid PointCancelCondition condition) {
        condition.setMemberId(memberId);
        return pointService.cancelPoint(condition);
    }

}
