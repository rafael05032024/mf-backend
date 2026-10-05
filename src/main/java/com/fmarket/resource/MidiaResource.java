package com.fmarket.resource;

import com.fmarket.dto.GetMidiaResponseDTO;
import com.fmarket.security.JwtTokenReader;
import com.fmarket.service.GetMidiaService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/midia")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MidiaResource {

    private static final String TOKEN_COOKIE = "Token";

    @Inject
    GetMidiaService getMidiaService;

    @Inject
    JwtTokenReader tokenReader;

    @GET
    @Path("/{q}")
    @Produces(MediaType.WILDCARD)
    public Response getMidia(@PathParam("q") String q,
            @CookieParam(TOKEN_COOKIE) String token) {
        // Autenticação opcional: sem cookie = anônimo; token presente porém inválido = 401.
        Long userId = token == null ? null : tokenReader.readUserIdFromToken(token);

        GetMidiaResponseDTO stream = getMidiaService.get(q, userId);

        return Response.ok(stream.stream())
                .type(stream.contentType())
                .header("Content-Disposition", "inline")
                .header("X-Content-Type-Options", "nosniff")
                .build();
    }
}
