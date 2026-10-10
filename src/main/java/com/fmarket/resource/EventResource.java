package com.fmarket.resource;

import com.fmarket.exception.InvalidTokenException;
import com.fmarket.service.EventService;

import io.quarkus.websockets.next.CloseReason;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.PathParam;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;

/**
 * WebSocket de eventos em tempo real. O frontend conecta em
 * {@code /event/{id}}, onde {@code id} é o profile do
 * usuário. A autenticação usa o cookie {@code Token} (ou o header
 * Authorization) enviado no handshake.
 */
@WebSocket(path = "/event/{id}")
public class EventResource {

    private static final String TOKEN_COOKIE = "Token";
    private static final String BEARER = "Bearer ";

    @Inject
    EventService eventService;

    @OnOpen
    @Blocking
    public void onOpen(WebSocketConnection connection, @PathParam String id) {
        try {
            eventService.authorize(id, readToken(connection));
        } catch (InvalidTokenException e) {
            connection.closeAndAwait(new CloseReason(1008, e.getMessage()));
        }
    }

    private String readToken(WebSocketConnection connection) {
        String authorization = connection.handshakeRequest().header("Authorization");
        if (authorization != null && authorization.regionMatches(true, 0, BEARER, 0, BEARER.length())) {
            return authorization.substring(BEARER.length()).trim();
        }

        String cookies = connection.handshakeRequest().header("Cookie");
        if (cookies != null) {
            for (String cookie : cookies.split(";")) {
                String[] pair = cookie.trim().split("=", 2);
                if (pair.length == 2 && TOKEN_COOKIE.equals(pair[0])) {
                    return pair[1];
                }
            }
        }
        return null;
    }
}
