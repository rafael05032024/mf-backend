package com.fmarket.repository;

import com.fmarket.model.RechargeRequestModel;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RechargeRequestRepository implements PanacheRepository<RechargeRequestModel> {
}
