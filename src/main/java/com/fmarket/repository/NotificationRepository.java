package com.fmarket.repository;

import com.fmarket.model.NotificationModel;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NotificationRepository implements PanacheRepository<NotificationModel> {
}
