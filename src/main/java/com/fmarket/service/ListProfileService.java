package com.fmarket.service;

import com.fmarket.dto.ListProfileResponseDTO;
import com.fmarket.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ListProfileService {

    @Inject
    UserRepository userRepository;

    public List<ListProfileResponseDTO> list(String query) {
        return userRepository.findVerifiedByProfile(query).stream()
                .map(user -> new ListProfileResponseDTO(user.profile, user.thumb, user.coverPhoto, user.verified,
                        Boolean.TRUE.equals(user.highlighted)))
                .toList();
    }
}
