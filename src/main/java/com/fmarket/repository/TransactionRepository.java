package com.fmarket.repository;

import com.fmarket.model.TransactionModel;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TransactionRepository implements PanacheRepository<TransactionModel> {
}
