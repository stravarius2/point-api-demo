package com.example.demo.repository.point;

import com.example.demo.meta.PointActionType;
import com.example.demo.repository.point.entity.PointBalanceId;
import com.example.demo.repository.point.entity.PointHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistoryEntity, Long> {

    @Query("select ph from PointHistoryEntity ph join ph.pointBalanceEntity pb where pb.memberId = :memberId order by ph.id desc")
    Page<PointHistoryEntity> findAllByMemberIdOrderByIdDesc(Long memberId, PageRequest pageRequest);

    @Query("select ph from PointHistoryEntity ph join ph.pointBalanceEntity pb where pb.memberId = :memberId order by ph.id desc")
    List<PointHistoryEntity> findAllByMemberId(Long memberId);

    @Query("select ph from PointHistoryEntity ph join ph.pointBalanceEntity pb where pb.memberId = :memberId and ph.id = :id and ph.pointActionType = :pointActionType")
    Optional<PointHistoryEntity> findByMemberIdAndIdAndPointActionType(Long memberId, Long id, PointActionType pointActionType);


}
