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

    private static final String USER_ID_PARAM = "id";

    @Inject
    JwtTokenReader tokenReader;

    @Inject
    UserRepository userRepository;

    @Inject
    OpenConnections connections;

    /** Garante que o token pertence ao usuário informado; caso contrário lança {@link InvalidTokenException}. */
    @Transactional
    public void authorize(String id, String token) {
        Long tokenUserId = tokenReader.readUserIdFromToken(token);

        boolean owner = tokenUserId.toString().equals(id) && userRepository.findActiveById(tokenUserId).isPresent();
        if (!owner) {
            throw new InvalidTokenException("Token não pertence ao usuário informado");
        }
    }

    /** Envia o evento a todas as conexões abertas do usuário e retorna quantas o receberam. */
    public int publish(Long userId, EventResponseDTO event) {
        String id = String.valueOf(userId);
        int delivered = 0;
        for (WebSocketConnection connection : connections) {
            if (id.equals(connection.pathParam(USER_ID_PARAM))) {
                connection.sendText(event).subscribe().with(v -> { }, e -> { });
                delivered++;
            }
        }
        return delivered;
    }
}
