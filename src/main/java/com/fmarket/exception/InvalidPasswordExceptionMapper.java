package com.fmarket.exception;

import com.fmarket.dto.LoginResponseDTO;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InvalidPasswordExceptionMapper implements ExceptionMapper<InvalidPasswordException> {

    @Override
    public Response toResponse(InvalidPasswordException e) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .type(MediaType.APPLICATION_JSON)
                .entity(LoginResponseDTO.ofMessage(e.getMessage()))
                .build();
    }
}
