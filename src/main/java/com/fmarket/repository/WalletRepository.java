package com.fmarket.repository;

import com.fmarket.model.WalletModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class WalletRepository implements PanacheRepository<WalletModel> {

    public Optional<WalletModel> findByOwner(Long owner) {
        return find("owner", owner).firstResultOptional();
    }
}
