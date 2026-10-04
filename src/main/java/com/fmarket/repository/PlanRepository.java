package com.fmarket.repository;

import java.util.Optional;

import com.fmarket.model.PlanModel;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PlanRepository implements PanacheRepository<PlanModel> {

    public Optional<PlanModel> findByProducer(Long producer) {
        return find("producer", producer).firstResultOptional();
    }
}
