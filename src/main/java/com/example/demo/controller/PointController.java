package com.example.demo.controller;

import com.example.demo.model.PointBalance;
import com.example.demo.model.PointHistory;
import com.example.demo.model.condition.PointCancelCondition;
import com.example.demo.model.condition.PointCondition;
import com.example.demo.model.condition.PointHistoryCondition;
import com.example.demo.service.PointService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
    @ApiOperation(value = "포인트 조회", response = PointBalance.class)
    public PointBalance getPoint(@RequestHeader("memberId") @ApiParam("회원아이디") Long memberId) {
        return pointService.getTotalPoint(memberId);
    }

    @GetMapping("/point/history")
    @ApiOperation(value = "포인트 내역 조회", response = PointHistory.class)
    public Page<PointHistory> getPointHistory(@RequestHeader("memberId") @ApiParam("회원아이디") Long memberId, @ModelAttribute @Valid PointHistoryCondition condition) {
        condition.setMemberId(memberId);
        return pointService.getPointHistory(condition);
    }

    @PostMapping("/point/save")
    @ApiOperation(value = "포인트 적립", response = PointBalance.class)
    public PointBalance savePoint(@RequestHeader("memberId") @ApiParam("회원아이디") Long memberId, @RequestBody PointCondition condition) {
        condition.setMemberId(memberId);
        condition.setPointActionType(SAVE);
        return pointService.setPoint(condition);
    }

    @PostMapping("/point/use")
    @ApiOperation(value = "포인트 사용", response = PointBalance.class)
    public PointBalance usePoint(@RequestHeader("memberId") @ApiParam("회원아이디") Long memberId, @RequestBody PointCondition condition) {
        condition.setMemberId(memberId);
        condition.setPointActionType(USE);
        return pointService.setPoint(condition);
    }

    @DeleteMapping("/point/cancel")
    @ApiOperation(value = "포인트 사용 취소(Rollback)", response = PointBalance.class)
    public PointBalance cancelPoint(@RequestHeader("memberId") @ApiParam("회원아이디") Long memberId, @ModelAttribute @Valid PointCancelCondition condition) {
        condition.setMemberId(memberId);
        return pointService.cancelPoint(condition);
    }

}
