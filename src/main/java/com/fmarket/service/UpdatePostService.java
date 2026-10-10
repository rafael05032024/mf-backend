package com.fmarket.service;

import java.io.IOException;
import java.time.LocalDateTime;

import com.fmarket.dto.StoreMidiaRequestDTO;
import com.fmarket.dto.UpdatePostRequestDTO;
import com.fmarket.exception.ActionForbiddenException;
import com.fmarket.exception.PostNotFoundException;
import com.fmarket.model.UserPost;
import com.fmarket.provider.BlobStorageProvider;
import com.fmarket.repository.UserPostRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class UpdatePostService {

    @Inject
    UserPostRepository userPostRepository;

    @Inject
    PostMidiaValidator postMidiaValidator;

    @Inject
    BlobStorageProvider blobStorageProvider;

    @Transactional
    public void update(Long userId, Long postId, UpdatePostRequestDTO request) throws IOException {
        UserPost post = userPostRepository.findByIdOptional(postId)
                .orElseThrow(() -> new PostNotFoundException("Post inexistente"));

        if (!userId.equals(post.owner)) {
            throw new ActionForbiddenException("Ação não permitida");
        }

        boolean isPrivate = postMidiaValidator.parseIsPrivate(request.isPrivate());
        String description = postMidiaValidator.parseDescription(request.description());

        if (hasMidia(request)) {
            var midia = postMidiaValidator.validate(request.midia());
            blobStorageProvider.store(new StoreMidiaRequestDTO(request.midia(), midia.fileName()));
            post.type = midia.type();
            post.content = midia.fileName();
        }

        post.isPrivate = isPrivate;
        post.description = description;
        post.updatedAt = LocalDateTime.now();
    }

    private boolean hasMidia(UpdatePostRequestDTO request) {
        return request.midia() != null && request.midia().uploadedFile() != null && request.midia().size() > 0;
    }
}
