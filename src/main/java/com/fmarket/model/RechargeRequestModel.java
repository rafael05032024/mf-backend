package com.fmarket.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "recharge_request")
public class RechargeRequestModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "user_id", nullable = false)
    public Long userId;

    @Column(nullable = false)
    public BigDecimal value;

    @Column(columnDefinition = "TEXT")
    public String reference;

    @Column(name = "expired_at")
    public LocalDateTime expiredAt;

    @Column(name = "processed_at")
    public LocalDateTime processedAt;

    @Column(name = "created_at")
    public LocalDateTime createdAt = LocalDateTime.now();
}
