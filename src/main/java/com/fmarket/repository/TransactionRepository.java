package com.fmarket.repository;

import com.fmarket.model.TransactionModel;

import java.util.List;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TransactionRepository implements PanacheRepository<TransactionModel> {

    public List<TransactionModel> findByWalletId(Long walletId) {
        return list("walletId", Sort.descending("createdAt", "id"), walletId);
    }
}
