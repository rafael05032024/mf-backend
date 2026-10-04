package com.fmarket.exception;

import com.fmarket.dto.UpdateAccountResponseDTO;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InvalidImageExceptionMapper implements ExceptionMapper<InvalidImageException> {

    @Override
    public Response toResponse(InvalidImageException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(new UpdateAccountResponseDTO(e.getMessage()))
                .build();
    }
}
