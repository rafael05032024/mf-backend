package com.fmarket.repository;

import java.util.List;
import java.util.Optional;

import com.fmarket.model.PostType;
import com.fmarket.model.UserPost;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserPostRepository implements PanacheRepository<UserPost> {

    public List<UserPost> findByOwner(Long owner) {
        return list("owner", Sort.descending("createdAt"), owner);
    }

    public Optional<UserPost> findByContent(String content) {
        return find("content", content).firstResultOptional();
    }

    public long countPrivateByOwner(Long owner) {
        return count("owner = ?1 and isPrivate = true", owner);
    }

    public long countByOwnerAndType(Long owner, PostType type) {
        return count("owner = ?1 and type = ?2", owner, type);
    }
}
