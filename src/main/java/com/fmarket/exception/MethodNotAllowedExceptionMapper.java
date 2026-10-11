package com.fmarket.exception;

import com.fmarket.dto.CreateAccountResponseDTO;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class MethodNotAllowedExceptionMapper implements ExceptionMapper<NotAllowedException> {

    @Override
    public Response toResponse(NotAllowedException e) {
        return Response.fromResponse(e.getResponse())
                .type(MediaType.APPLICATION_JSON)
                .entity(new CreateAccountResponseDTO("Método HTTP não permitido para esta rota"))
                .build();
    }
}
