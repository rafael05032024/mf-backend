package com.fmarket.repository;

import java.util.UUID;

import com.fmarket.model.SessionModel;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SessionRepository implements PanacheRepositoryBase<SessionModel, UUID> {
}
