package com.fmarket.exception;

import com.fmarket.dto.CreateAccountResponseDTO;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class PostNotFoundExceptionMapper implements ExceptionMapper<PostNotFoundException> {

    @Override
    public Response toResponse(PostNotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity(new CreateAccountResponseDTO(e.getMessage()))
                .build();
    }
}
