package com.fmarket.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "session")
public class SessionModel {

    @Id
    @Column(name = "session_id")
    public UUID sessionId = UUID.randomUUID();

    @Column(name = "expire_at", nullable = false)
    public LocalDateTime expireAt;

    @Column(name = "created_at")
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    public LocalDateTime updatedAt = LocalDateTime.now();
}
