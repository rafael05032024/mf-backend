package com.fmarket.repository;

import java.time.LocalDateTime;

import com.fmarket.model.SignatureModel;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SignatureRepository implements PanacheRepository<SignatureModel> {

    public boolean existsActive(Long subscriber, Long producer, LocalDateTime now) {
        return count("subscriber = ?1 and producer = ?2 and expireAt > ?3", subscriber, producer, now) > 0;
    }
}
