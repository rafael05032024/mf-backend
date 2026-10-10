package com.fmarket.repository;

import java.util.Optional;

import com.fmarket.model.RegistrationRequestModel;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RegistrationRequestRepository implements PanacheRepository<RegistrationRequestModel> {

    public long countByEmail(String email) {
        return count("email", email);
    }

    public Optional<RegistrationRequestModel> findLatestByEmail(String email) {
        return find("lower(email) = lower(?1) order by createdAt desc, id desc", email).firstResultOptional();
    }
}
