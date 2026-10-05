package com.fmarket.resource;

import com.fmarket.dto.ListTransactionsResponseDTO;
import com.fmarket.security.Authenticated;
import com.fmarket.service.ListTransactionsService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/wallet/transactions")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
public class ListTransactionsResource {

    @Inject
    ListTransactionsService listTransactionsService;

    @GET
    public ListTransactionsResponseDTO list(@Context SecurityContext security) {
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        return listTransactionsService.list(userId);
    }
}
