package com.fmarket.service;

import com.fmarket.dto.RequestCreateAccountDTO;
import com.fmarket.exception.BusinessConflictException;
import com.fmarket.model.UserModel;
import com.fmarket.repository.UserRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateAccountService {

    @Inject
    UserRepository userRepository;

    @Transactional
    public void create(RequestCreateAccountDTO request) {
        String email = request.email().trim();
        String profile = request.profile().trim();

        if (userRepository.existsByEmail(email)) {
            throw new BusinessConflictException("E-mail já cadastrado");
        }
        if (userRepository.existsByProfile(profile)) {
            throw new BusinessConflictException("Profile já cadastrado");
        }

        UserModel user = new UserModel();
        user.name = request.name().trim();
        user.email = email;
        user.profile = profile;
        user.password = BcryptUtil.bcryptHash(request.password());
        userRepository.persist(user);
    }
}
