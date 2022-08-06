package com.example.demo.repository.point;

import com.example.demo.meta.PointActionType;
import com.example.demo.repository.point.entity.ExpiredTarget;
import com.example.demo.repository.point.entity.PointHistoryDetailEntity;
import com.example.demo.repository.point.entity.PointHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PointHistoryDetailRepository extends JpaRepository<PointHistoryDetailEntity, Long> {

    List<PointHistoryDetailEntity> findAllByPointHistoryDetailIdAndPointActionType(Long pointHistoryDetailId, PointActionType pointActionType);

    @Query("SELECT pd FROM PointHistoryDetailEntity pd JOIN pd.pointHistoryEntity ph WHERE pd.pointHistoryEntity.id = :pointHistoryId ORDER BY pd.id DESC")
    List<PointHistoryDetailEntity> findAllByPointHistoryId(Long pointHistoryId);

    @Query(value = "SELECT t.* FROM (" +
            "SELECT point_history_Detail_id AS id, min(point_history_id) AS pointHistoryId, sum(point) AS point, min(expired_at) AS expiredAt FROM  POINT_HISTORY_DETAIL GROUP BY point_history_detail_id"
            +") t WHERE t.expiredAt < now() AND t.point > 0", nativeQuery = true)
    List<ExpiredTarget> findAllByExpiredAt();
}
