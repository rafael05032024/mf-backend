package com.fmarket.repository;

import com.fmarket.model.UserModel;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<UserModel> {

    public boolean existsByEmail(String email) {
        return count("lower(email)", email.toLowerCase()) > 0;
    }

    public boolean existsByProfile(String profile) {
        return count("lower(profile)", profile.toLowerCase()) > 0;
    }

    public Optional<UserModel> findActiveByEmail(String email) {
        return find("lower(email) = ?1 and deletedAt is null", email.toLowerCase()).firstResultOptional();
    }

    public boolean existsByProfileAndIdNot(String profile, Long id) {
        return count("lower(profile) = ?1 and id <> ?2", profile.toLowerCase(), id) > 0;
    }

    public Optional<UserModel> findActiveById(Long id) {
        return find("id = ?1 and deletedAt is null", id).firstResultOptional();
    }

    public Optional<UserModel> findByProfile(String profile) {
        return find("lower(profile)", profile.toLowerCase()).firstResultOptional();
    }

    public Optional<UserModel> findActiveByProfile(String profile) {
        return find("lower(profile) = ?1 and deletedAt is null", profile.toLowerCase()).firstResultOptional();
    }

    public List<UserModel> findVerifiedByProfile(String query) {
        if (query == null || query.isBlank()) {
            return list("verified = true and deletedAt is null");
        }
        return list("verified = true and deletedAt is null and lower(profile) like ?1",
                "%" + query.trim().toLowerCase() + "%");
    }

    public boolean existsByThumbOrCoverPhoto(String fileName) {
        return count("thumb = ?1 or coverPhoto = ?1", fileName) > 0;
    }
}
