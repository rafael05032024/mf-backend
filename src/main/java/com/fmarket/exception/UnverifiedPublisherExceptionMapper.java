package com.fmarket.exception;

import com.fmarket.dto.CreateAccountResponseDTO;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UnverifiedPublisherExceptionMapper implements ExceptionMapper<UnverifiedPublisherException> {

    @Override
    public Response toResponse(UnverifiedPublisherException e) {
        return Response.status(Response.Status.FORBIDDEN)
                .type(MediaType.APPLICATION_JSON)
                .entity(new CreateAccountResponseDTO(e.getMessage()))
                .build();
    }
}
