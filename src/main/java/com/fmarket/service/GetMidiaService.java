package com.fmarket.service;

import java.time.LocalDateTime;

import com.fmarket.dto.GetMidiaResponseDTO;
import com.fmarket.exception.MidiaForbiddenException;
import com.fmarket.exception.MidiaNotFoundException;
import com.fmarket.model.UserPost;
import com.fmarket.provider.BlobStorageProvider;
import com.fmarket.repository.SignatureRepository;
import com.fmarket.repository.UserPostRepository;
import com.fmarket.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetMidiaService {

    private static final String FORBIDDEN_MESSAGE = "Você não tem permissão para ver este conteúdo";

    @Inject
    UserPostRepository userPostRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    SignatureRepository signatureRepository;

    @Inject
    BlobStorageProvider blobStorageProvider;

    /** @param userId id do usuário logado, ou {@code null} se anônimo */
    public GetMidiaResponseDTO get(String midia, Long userId) {
        // thumb e cover_photo são públicos: não há post associado, busca direto no storage.
        if (userRepository.existsByThumbOrCoverPhoto(midia)) {
            return blobStorageProvider.getMidia(midia);
        }

        UserPost post = userPostRepository.findByContent(midia)
                .orElseThrow(() -> new MidiaNotFoundException("Mídia inexistente"));

        if (Boolean.TRUE.equals(post.isPrivate)
                && (userId == null || !canViewPrivate(post, userId))) {
            throw new MidiaForbiddenException(FORBIDDEN_MESSAGE);
        }

        return blobStorageProvider.getMidia(post.content);
    }

    private boolean canViewPrivate(UserPost post, Long userId) {
        return post.owner.equals(userId)
                || signatureRepository.existsActive(userId, post.owner, LocalDateTime.now());
    }
}
