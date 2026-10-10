package com.fmarket.service;

import com.fmarket.dto.EventResponseDTO;
import com.fmarket.exception.InvalidTokenException;
import com.fmarket.repository.UserRepository;
import com.fmarket.security.JwtTokenReader;

import io.quarkus.websockets.next.OpenConnections;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class EventService {

    private static final String PROFILE_PARAM = "id";

    @Inject
    JwtTokenReader tokenReader;

    @Inject
    UserRepository userRepository;

    @Inject
    OpenConnections connections;

    /** Garante que o token pertence ao dono do profile informado; caso contrário lança {@link InvalidTokenException}. */
    @Transactional
    public void authorize(String profile, String token) {
        Long userId = tokenReader.readUserIdFromToken(token);

        boolean owner = userRepository.findActiveById(userId)
                .map(user -> user.profile.equalsIgnoreCase(profile))
                .orElse(false);
        if (!owner) {
            throw new InvalidTokenException("Token não pertence ao profile informado");
        }
    }

    /** Envia o evento a todas as conexões abertas do profile e retorna quantas o receberam. */
    public int publish(String profile, EventResponseDTO event) {
        int delivered = 0;
        for (WebSocketConnection connection : connections) {
            if (profile.equalsIgnoreCase(connection.pathParam(PROFILE_PARAM))) {
                connection.sendText(event).subscribe().with(v -> { }, e -> { });
                delivered++;
            }
        }
        return delivered;
    }
}
