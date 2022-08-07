package com.example.demo.provider;

import com.example.demo.exception.PointNotEnoughException;
import com.example.demo.meta.PointActionType;
import com.example.demo.model.condition.PointCancelCondition;
import com.example.demo.model.condition.PointCondition;
import com.example.demo.repository.point.PointBalanceRepository;
import com.example.demo.repository.point.PointHistoryDetailRepository;
import com.example.demo.repository.point.PointHistoryRepository;
import com.example.demo.repository.point.entity.PointBalanceEntity;
import com.example.demo.repository.point.entity.PointHistoryDetailEntity;
import com.example.demo.repository.point.entity.PointHistoryEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.meta.Common.POINT_NOT_ENOUGH_MESSAGE;
import static com.example.demo.meta.PointActionType.*;
import static com.example.demo.utils.CommonUtil.getExpiredDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointProvider {

    private final PointBalanceRepository pointBalanceRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PointHistoryDetailRepository pointHistoryDetailRepository;
    private final PointExpireProvider pointExpireProvider;

    public PointBalanceEntity getTotalPoint(Long memberId){
        return pointBalanceRepository.findByMemberId(memberId).orElseGet(PointBalanceEntity::new);
    }

    public Page<PointHistoryEntity> getPointHistory(Long memberId, PageRequest pageRequest){
        return pointHistoryRepository.findAllByMemberIdOrderByIdDesc(memberId, pageRequest);
    }

    @Transactional(rollbackFor = Exception.class)
    public PointBalanceEntity setPoint(PointCondition condition){

        PointBalanceEntity pointBalanceEntity = pointBalanceRepository.findByMemberId(condition.getMemberId()).orElseGet(PointBalanceEntity::new);
        pointBalanceEntity.setMemberId(condition.getMemberId());

        long point = condition.getPoint();
        long expiredPoint = 0L;
        PointActionType pointActionType = condition.getPointActionType();

        long totalPoint = pointBalanceEntity.getTotalPoint() + point;
        if(totalPoint < 0L){
            throw new PointNotEnoughException(POINT_NOT_ENOUGH_MESSAGE);
        }

        PointHistoryEntity pointHistoryEntity = new PointHistoryEntity();
        pointHistoryEntity.setPointBalanceEntity(pointBalanceEntity);
        pointHistoryEntity.setPointActionType(pointActionType);
        pointHistoryEntity.setPoint(point);
        pointHistoryRepository.save(pointHistoryEntity);

        if(SAVE.equals(pointActionType)){
            PointHistoryDetailEntity pointHistoryDetailEntity = new PointHistoryDetailEntity();
            pointHistoryDetailEntity.setPointHistoryEntity(pointHistoryEntity);
            pointHistoryDetailEntity.setPointActionType(pointActionType);
            pointHistoryDetailEntity.setPoint(point);
            pointHistoryDetailEntity.setExpiredAt(getExpiredDate());
            pointHistoryDetailEntity.setIsExpired(false);
            pointHistoryDetailRepository.save(pointHistoryDetailEntity);

        }else if(USE.equals(pointActionType)){
            expiredPoint = usePointAndGetIfPointExpired(pointBalanceEntity, pointHistoryEntity, point);
        }

        totalPoint = totalPoint + expiredPoint;
        if(totalPoint < 0L){
            throw new PointNotEnoughException(POINT_NOT_ENOUGH_MESSAGE);
        }

        pointBalanceEntity.setTotalPoint(totalPoint + expiredPoint);
        pointBalanceRepository.save(pointBalanceEntity);

        return pointBalanceEntity;
    }

    @Transactional(rollbackFor = Exception.class)
    public PointBalanceEntity cancelPoint(PointCancelCondition condition){
        PointHistoryEntity pointHistoryEntity = pointHistoryRepository.findByMemberIdAndIdAndPointActionType(condition.getMemberId(), condition.getId(), USE).orElseThrow(EntityNotFoundException::new);
        pointHistoryEntity.getPointBalanceEntity().setTotalPoint(pointHistoryEntity.getPointBalanceEntity().getTotalPoint() + (pointHistoryEntity.getPoint() * -1));
        pointHistoryRepository.deleteById(pointHistoryEntity.getId());
        return pointHistoryEntity.getPointBalanceEntity();
    }



    private long usePointAndGetIfPointExpired(PointBalanceEntity pointBalanceEntity, PointHistoryEntity pointHistoryEntity, long point){

        long expiredPoint = 0L;
        point = Math.abs(point);
        List<PointHistoryEntity> historyEntities = pointBalanceEntity.getPointHistoryEntities().stream()
                .filter(entity -> !EXPIRED.equals(entity.getPointActionType()))
                .collect(Collectors.toList());
        int lastIndex = historyEntities.size() - 1;

        for (int i = 0; i < historyEntities.size(); i++) {
            if (SAVE.equals(historyEntities.get(i).getPointActionType()) && point > 0L) {

                Long historyDetailId = 0L;
                long remainPoint = 0L;
                long deductPoint;

                for (PointHistoryDetailEntity detailEntity : historyEntities.get(i).getPointHistoryDetailEntities()) {
                    if (SAVE.equals(detailEntity.getPointActionType()) && !detailEntity.getIsExpired()) {
                        if (LocalDate.now().isBefore(detailEntity.getExpiredAt())) {
                            historyDetailId = detailEntity.getId();
                            remainPoint = detailEntity.getPoint();
                        } else {
                            expiredPoint = pointExpireProvider.expireAndGetExpiredPoint(detailEntity.getId(), historyEntities.get(i).getId(), pointBalanceEntity.getId());
                            break;
                        }
                        for(PointHistoryDetailEntity usedEntity : pointHistoryDetailRepository.findAllByPointHistoryDetailIdAndPointActionType(historyDetailId, USE)){
                            remainPoint += usedEntity.getPoint();
                        }
                    }
                }

                if (remainPoint > 0) {
                    if (remainPoint >= point) {
                        deductPoint = point * -1;
                        point = 0L;
                    } else {
                        if (i == lastIndex) {
                            throw new PointNotEnoughException(POINT_NOT_ENOUGH_MESSAGE);
                        }
                        deductPoint = remainPoint * -1;
                        point -= remainPoint;
                    }
                } else {
                    if (i == lastIndex) {
                        throw new PointNotEnoughException(POINT_NOT_ENOUGH_MESSAGE);
                    } else {
                        continue;
                    }
                }

                PointHistoryDetailEntity pointHistoryDetailEntity = new PointHistoryDetailEntity();
                pointHistoryDetailEntity.setPointHistoryEntity(pointHistoryEntity);
                pointHistoryDetailEntity.setPointActionType(USE);
                pointHistoryDetailEntity.setPoint(deductPoint);
                pointHistoryDetailEntity.setPointHistoryDetailId(historyDetailId);
                pointHistoryDetailRepository.save(pointHistoryDetailEntity);

            }
        }
        return expiredPoint;
    }


}
