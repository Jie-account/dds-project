package com.team9.fitness.repository;

import com.team9.fitness.entity.AlertMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 警报信息仓库
 */
@Repository
public interface AlertMessageRepository extends JpaRepository<AlertMessage, Long> {
    
    /**
     * 查找未读的警报信息
     */
    List<AlertMessage> findByIsReadFalseOrderByCreatedAtDesc();
    
    /**
     * 查找指定时间范围内的警报信息
     */
    List<AlertMessage> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据级别查找警报信息
     */
    List<AlertMessage> findByLevelOrderByCreatedAtDesc(AlertMessage.AlertLevel level);
    
    /**
     * 标记警报为已读
     */
    @Modifying
    @Query("UPDATE AlertMessage a SET a.isRead = true WHERE a.id = :id")
    void markAsRead(@Param("id") Long id);
    
    /**
     * 标记所有警报为已读
     */
    @Modifying
    @Query("UPDATE AlertMessage a SET a.isRead = true WHERE a.isRead = false")
    void markAllAsRead();
    
    /**
     * 统计未读警报数量
     */
    long countByIsReadFalse();
    
    /**
     * 删除指定时间之前的警报信息
     */
    void deleteByCreatedAtBefore(LocalDateTime time);
}
