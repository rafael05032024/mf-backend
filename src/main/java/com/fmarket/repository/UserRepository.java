package com.fmarket.repository;

import com.fmarket.model.UserModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<UserModel> {

    public boolean existsByEmail(String email) {
        return count("lower(email)", email.toLowerCase()) > 0;
    }

    public boolean existsByProfile(String profile) {
        return count("lower(profile)", profile.toLowerCase()) > 0;
    }
}
