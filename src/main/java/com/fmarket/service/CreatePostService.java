package com.fmarket.service;

import java.io.IOException;
import java.time.LocalDateTime;

import com.fmarket.dto.CreatePostRequestDTO;
import com.fmarket.dto.CreatePostResponseDTO;
import com.fmarket.dto.StoreMidiaRequestDTO;
import com.fmarket.exception.InvalidImageException;
import com.fmarket.exception.UnverifiedPublisherException;
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

        if (!Boolean.TRUE.equals(user.verified)) {
            throw new UnverifiedPublisherException(
                    "Você não pode postar conteúdo porque não possui uma conta de publicador verificada");
        }

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
}
