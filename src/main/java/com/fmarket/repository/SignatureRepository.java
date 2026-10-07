package com.fmarket.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.fmarket.model.SignatureModel;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SignatureRepository implements PanacheRepository<SignatureModel> {

    public boolean existsActive(Long subscriber, Long producer, LocalDateTime now) {
        return count("subscriber = ?1 and producer = ?2 and expireAt > ?3", subscriber, producer, now) > 0;
    }

    public long countActiveBySubscriber(Long subscriber, LocalDateTime now) {
        return count("subscriber = ?1 and expireAt > ?2", subscriber, now);
    }

    public long countActiveByProducer(Long producer, LocalDateTime now) {
        return count("producer = ?1 and expireAt > ?2", producer, now);
    }

    public List<SignatureModel> listActiveBySubscriber(Long subscriber, LocalDateTime now) {
        return list("subscriber = ?1 and expireAt > ?2 order by expireAt", subscriber, now);
    }
}
