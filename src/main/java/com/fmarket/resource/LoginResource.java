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
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

import java.time.Duration;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/api/login")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

    private static final String TOKEN_COOKIE = "Token";

    @Inject
    LoginService loginService;

    @ConfigProperty(name = "app.jwt.expiration")
    Duration expiration;

    @ConfigProperty(name = "app.jwt.cookie-secure")
    boolean cookieSecure;

    @POST
    public Response login(@Valid LoginRequestDTO request) {
        LoginResponseDTO body = loginService.login(request);

        NewCookie cookie = new NewCookie.Builder(TOKEN_COOKIE)
                .value(body.token())
                .path("/")
                .maxAge((int) expiration.toSeconds())
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(NewCookie.SameSite.LAX)
                .build();

        return Response.ok(body).cookie(cookie).build();
    }
}
