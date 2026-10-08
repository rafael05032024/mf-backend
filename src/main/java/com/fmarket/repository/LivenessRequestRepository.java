package com.fmarket.repository;

import com.fmarket.model.LivenessRequestModel;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LivenessRequestRepository implements PanacheRepository<LivenessRequestModel> {
}
