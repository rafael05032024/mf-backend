package com.fmarket.resource;

import com.fmarket.dto.CreateAccountRequestDTO;
import com.fmarket.service.CreateAccountService;

import io.vertx.core.http.HttpServerRequest;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/accounts")
@Consumes(MediaType.APPLICATION_JSON)
public class CreateAccountResource {

    @Inject
    CreateAccountService createAccountService;

    @Inject
    ClientIpResolver clientIpResolver;

    @POST
    public Response create(@Valid CreateAccountRequestDTO request, @Context HttpServerRequest httpRequest) {
        createAccountService.create(request, clientIpResolver.resolve(httpRequest));
        return Response.noContent().build();
    }
}
