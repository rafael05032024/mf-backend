package com.fmarket.service;

import com.fmarket.exception.ActionForbiddenException;
import com.fmarket.exception.PostNotFoundException;
import com.fmarket.model.UserPost;
import com.fmarket.repository.UserPostRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DeletePostService {

    @Inject
    UserPostRepository userPostRepository;

    @Transactional
    public void delete(Long userId, Long postId) {
        UserPost post = userPostRepository.findByIdOptional(postId)
                .orElseThrow(() -> new PostNotFoundException("Post inexistente"));

        if (!userId.equals(post.owner)) {
            throw new ActionForbiddenException("Ação não permitida");
        }

        userPostRepository.delete(post);
    }
}
