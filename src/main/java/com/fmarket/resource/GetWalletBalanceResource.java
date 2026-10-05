package com.fmarket.resource;

import com.fmarket.dto.GetWalletBalanceResponseDTO;
import com.fmarket.security.Authenticated;
import com.fmarket.service.GetWalletBalanceService;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api/wallet/balance")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
public class GetWalletBalanceResource {

    @Inject
    GetWalletBalanceService getWalletBalanceService;

    @GET
    public GetWalletBalanceResponseDTO get(@Context SecurityContext security) {
        Long userId = Long.valueOf(security.getUserPrincipal().getName());
        return getWalletBalanceService.get(userId);
    }
}
