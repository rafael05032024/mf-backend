package com.fmarket.resource;

import com.fmarket.dto.CreateAccountRequestDTO;
import com.fmarket.service.CreateAccountService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/accounts")
@Consumes(MediaType.APPLICATION_JSON)
public class CreateAccountResource {

    @Inject
    CreateAccountService createAccountService;

    @POST
    public Response create(@Valid CreateAccountRequestDTO request) {
        createAccountService.create(request);
        return Response.noContent().build();
    }
}
