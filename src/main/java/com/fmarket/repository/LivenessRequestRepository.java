package com.fmarket.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import com.fmarket.model.LivenessRequestModel;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LivenessRequestRepository implements PanacheRepository<LivenessRequestModel> {

    public Optional<LivenessRequestModel> findActiveByUser(Long userId, LocalDateTime now) {
        return find("userId = ?1 and processedAt is null and expireAt > ?2 order by expireAt desc", userId, now)
                .firstResultOptional();
    }
}
