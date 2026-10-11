package com.fmarket.service;

import java.io.IOException;
import java.time.LocalDateTime;

import com.fmarket.dto.CreatePostRequestDTO;
import com.fmarket.dto.CreatePostResponseDTO;
import com.fmarket.dto.StoreMidiaRequestDTO;
import com.fmarket.exception.ActionForbiddenException;
import com.fmarket.exception.InvalidImageException;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.model.UserModel;
import com.fmarket.model.UserPost;
import com.fmarket.provider.BlobStorageProvider;
import com.fmarket.repository.UserPostRepository;
import com.fmarket.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreatePostService {

    private static final int LEVEL_LIMITED = 2;
    private static final int LEVEL_UNLIMITED = 3;
    private static final long LEVEL_LIMITED_MAX_POSTS = 15;

    @Inject
    UserRepository userRepository;

    @Inject
    UserPostRepository userPostRepository;

    @Inject
    PostMidiaValidator postMidiaValidator;

    @Inject
    BlobStorageProvider blobStorageProvider;

    @Transactional
    public CreatePostResponseDTO create(Long userId, CreatePostRequestDTO request) throws IOException {
        boolean isPrivate = postMidiaValidator.parseIsPrivate(request.isPrivate());
        String description = postMidiaValidator.parseDescription(request.description());
        var midia = postMidiaValidator.validate(request.midia());

        UserModel user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        validatePostLimit(user);

        String fileName = midia.fileName();

        blobStorageProvider.store(new StoreMidiaRequestDTO(request.midia(), fileName));

        UserPost post = new UserPost();
        post.owner = userId;
        post.type = midia.type();
        post.content = fileName;
        post.isPrivate = isPrivate;
        post.description = description;
        post.createdAt = LocalDateTime.now();
        post.updatedAt = post.createdAt;
        userPostRepository.persist(post);

        return new CreatePostResponseDTO(post.id, midia.type().name(), fileName, isPrivate, description);
    }

    private void validatePostLimit(UserModel user) {
        int level = user.level == null ? 1 : user.level;

        if (level >= LEVEL_UNLIMITED) {
            return;
        }
        if (level < LEVEL_LIMITED) {
            throw new ActionForbiddenException("Seu nível não permite criar posts");
        }
        if (userPostRepository.countByOwner(user.id) >= LEVEL_LIMITED_MAX_POSTS) {
            throw new ActionForbiddenException(
                    "Você já excedeu o número máximo de posts que pode fazer");
        }
    }
}
