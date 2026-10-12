package com.fmarket.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.fmarket.model.MessageModel;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MessageRepository implements PanacheRepository<MessageModel> {

    public List<MessageModel> findByParticipant(Long userId) {
        return list("toUserId = ?1 or fromUserId = ?1", Sort.ascending("createdAt", "id"), userId);
    }

    public int markAllAsRead(Long userId, LocalDateTime now) {
        return update("readedAt = ?1, updatedAt = ?1 where toUserId = ?2 and readedAt is null", now, userId);
    }
}
