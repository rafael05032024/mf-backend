package com.fmarket.resource;

import com.fmarket.dto.LoginRequestDTO;
import com.fmarket.dto.LoginResponseDTO;
import com.fmarket.service.LoginService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/login")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

    @Inject
    LoginService loginService;

    @POST
    public LoginResponseDTO login(@Valid LoginRequestDTO request) {
        return loginService.login(request);
    }
}
