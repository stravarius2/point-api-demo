package com.example.demo.provider;

import com.example.demo.repository.point.PointBalanceRepository;
import com.example.demo.repository.point.PointHistoryDetailRepository;
import com.example.demo.repository.point.PointHistoryRepository;
import com.example.demo.repository.point.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import java.util.List;

import static com.example.demo.meta.PointActionType.EXPIRED;
import static com.example.demo.meta.PointActionType.USE;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointExpireProvider {
    private final PointBalanceRepository pointBalanceRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PointHistoryDetailRepository pointHistoryDetailRepository;

    @Transactional
    public void expire(){
        long expiredPoint;
        long totalPoint;
        List<ExpiredTarget> detailEntityList = pointHistoryDetailRepository.findAllByExpiredAt();
        for(ExpiredTarget target : detailEntityList){
            try{
                PointHistoryEntity pointHistoryEntity = pointHistoryRepository.findById(target.getPointHistoryId()).orElseThrow(EntityNotFoundException::new);
                expiredPoint = expireAndGetExpiredPoint(target.getId(), target.getPointHistoryId(), pointHistoryEntity.getPointBalanceEntity().getId());
                totalPoint = pointHistoryEntity.getPointBalanceEntity().getTotalPoint();
                pointHistoryEntity.getPointBalanceEntity().setTotalPoint(totalPoint + expiredPoint);
            }catch(Exception e){
                log.error("[expire] point expire error", e);
            }
        }
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long expireAndGetExpiredPoint(Long detailHistoryId, Long historyId, Long pointBalanceId) {

        PointHistoryEntity historyEntity = pointHistoryRepository.findById(historyId)
                .orElseThrow(EntityNotFoundException::new);
        long usedPoint = pointHistoryDetailRepository.findAllByPointHistoryDetailIdAndPointActionType(detailHistoryId, USE)
                .stream().mapToLong(PointHistoryDetailEntity::getPoint).sum();

        long expiredPoint = (historyEntity.getPoint() + usedPoint) * -1;

        PointHistoryDetailEntity detailEntity = pointHistoryDetailRepository.findById(detailHistoryId)
                .orElseThrow(EntityNotFoundException::new);
        detailEntity.setIsExpired(true);
        pointHistoryDetailRepository.save(detailEntity);

        PointHistoryDetailEntity pointHistoryDetailEntity = new PointHistoryDetailEntity();
        pointHistoryDetailEntity.setPointHistoryDetailId(detailHistoryId);
        pointHistoryDetailEntity.setPointHistoryEntity(historyEntity);
        pointHistoryDetailEntity.setPointActionType(EXPIRED);
        pointHistoryDetailEntity.setPoint(expiredPoint);
        pointHistoryDetailRepository.save(pointHistoryDetailEntity);

        PointBalanceEntity balanceEntity = pointBalanceRepository.findById(pointBalanceId)
                .orElseThrow(EntityNotFoundException::new);

        PointHistoryEntity pointHistoryEntity = new PointHistoryEntity();
        pointHistoryEntity.setPointBalanceEntity(balanceEntity);
        pointHistoryEntity.setPointActionType(EXPIRED);
        pointHistoryEntity.setPoint(expiredPoint);
        pointHistoryRepository.save(pointHistoryEntity);

        return expiredPoint;
    }
}
