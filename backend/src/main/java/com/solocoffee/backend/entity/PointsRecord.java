package com.solocoffee.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "points_records")
public class PointsRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @Column(name = "points", nullable = false)
    private Integer points; // 正数为增加，负数为减少
    
    @Column(name = "type", nullable = false)
    private Integer type; // 1: 消费获得, 2: 积分兑换, 3: 活动奖励
    
    @Column(name = "related_id")
    private Long relatedId; // 关联ID（订单ID、兑换ID等）
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}