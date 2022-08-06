package com.example.demo.repository.point;

import com.example.demo.repository.point.entity.PointBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface PointBalanceRepository extends JpaRepository<PointBalanceEntity, Long> {

    Optional<PointBalanceEntity> findByMemberId(long memberId);

    @Transactional
    void deleteAllByMemberId(Long memberId);
}
