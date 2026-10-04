package com.fmarket.service;

import com.fmarket.dto.UpdateAccountRequestDTO;
import com.fmarket.dto.UpdateAccountResponseDTO;
import com.fmarket.exception.BusinessConflictException;
import com.fmarket.exception.NoDataToUpdateException;
import com.fmarket.exception.UserNotFoundException;
import com.fmarket.model.UserModel;
import com.fmarket.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@ApplicationScoped
public class UpdateAccountService {

    @Inject
    UserRepository userRepository;

    @Transactional
    public UpdateAccountResponseDTO update(Long userId, UpdateAccountRequestDTO request) {
        String realName = clean(request.realName());
        String document = clean(request.document());
        String name = clean(request.name());
        String profile = clean(request.profile());
        String description = clean(request.description());
        String tiktok = clean(request.tiktok());
        String instagram = clean(request.instagram());

        if (request.birthdate() == null && realName == null && document == null && name == null
                && profile == null && description == null && tiktok == null && instagram == null) {
            throw new NoDataToUpdateException("Não há dados a serem atualizados");
        }

        UserModel user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuário inexistente"));

        if (profile != null && userRepository.existsByProfileAndIdNot(profile, user.id)) {
            throw new BusinessConflictException("Já existe um usuário com o mesmo perfil");
        }

        if (realName != null) user.personalName = realName;
        if (request.birthdate() != null) user.birthdate = request.birthdate();
        if (document != null) user.document = document;
        if (name != null) user.name = name;
        if (profile != null) user.profile = profile;
        if (description != null) user.description = description;
        if (tiktok != null) user.tiktok = tiktok;
        if (instagram != null) user.instagram = instagram;
        user.updatedAt = LocalDateTime.now();

        return new UpdateAccountResponseDTO("Dados atualizados com sucesso");
    }

    private String clean(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
