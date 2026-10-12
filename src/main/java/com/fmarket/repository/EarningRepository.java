package com.fmarket.repository;

import com.fmarket.model.EarningModel;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EarningRepository implements PanacheRepository<EarningModel> {
}
