package com.fmarket.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.fmarket.model.ResetPassRequestModel;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ResetPassRequestRepository implements PanacheRepository<ResetPassRequestModel> {

    public long countByEmailSince(String email, LocalDateTime since) {
        return count("lower(email) = lower(?1) and createdAt >= ?2", email, since);
    }

    public Optional<ResetPassRequestModel> findLatestValidByEmail(String email, LocalDateTime now) {
        return find("lower(email) = lower(?1) and expireAt > ?2 and sessionId is null order by createdAt desc, id desc",
                email, now).firstResultOptional();
    }

    public Optional<ResetPassRequestModel> findBySessionId(UUID sessionId) {
        return find("sessionId", sessionId).firstResultOptional();
    }
}
