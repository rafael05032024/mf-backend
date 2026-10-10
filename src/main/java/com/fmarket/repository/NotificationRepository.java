package com.fmarket.repository;

import java.util.List;

import com.fmarket.model.NotificationModel;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NotificationRepository implements PanacheRepository<NotificationModel> {

    public List<NotificationModel> findByUserId(Long userId) {
        return list("userId", Sort.descending("createdAt", "id"), userId);
    }
}
