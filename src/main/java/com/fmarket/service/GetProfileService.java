package com.fmarket.service;

import java.math.BigDecimal;

import com.fmarket.dto.GetProfileResponseDTO;
import com.fmarket.dto.GetProfileResponseDTO.CountersDTO;
import com.fmarket.dto.GetProfileResponseDTO.PostDTO;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.model.PostType;
import com.fmarket.model.UserModel;
import com.fmarket.repository.PlanRepository;
import com.fmarket.repository.UserPostRepository;
import com.fmarket.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetProfileService {

    @Inject
    UserRepository userRepository;

    @Inject
    UserPostRepository userPostRepository;

    @Inject
    PlanRepository planRepository;

    public GetProfileResponseDTO get(String profile) {
        UserModel user = userRepository.findActiveByProfile(profile.trim())
                .orElseThrow(() -> new UserNotFoundException("Perfil inexistente"));

        var posts = userPostRepository.findByOwner(user.id).stream()
                .map(post -> new PostDTO(post.content, post.type.name().toLowerCase(), post.isPrivate))
                .toList();

        BigDecimal planValue = planRepository.findByProducer(user.id)
                .map(plan -> plan.value)
                .orElse(null);

        var counters = new CountersDTO(
                userPostRepository.countPrivateByOwner(user.id),
                userPostRepository.countByOwnerAndType(user.id, PostType.IMAGE),
                userPostRepository.countByOwnerAndType(user.id, PostType.VIDEO));

        return new GetProfileResponseDTO(user.name, user.profile, user.description, user.tiktok, user.instagram,
                user.verified, user.thumb, user.coverPhoto, posts, planValue, counters);
    }
}
