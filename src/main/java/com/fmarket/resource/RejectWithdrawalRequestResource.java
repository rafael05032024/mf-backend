package com.fmarket.resource;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fmarket.exception.ActionForbiddenException;
import com.fmarket.service.RejectWithdrawalRequestService;

import jakarta.inject.Inject;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/api/wallet/withdrawals/{id}/reject")
public class RejectWithdrawalRequestResource {

    @Inject
    RejectWithdrawalRequestService rejectWithdrawalRequestService;

    @ConfigProperty(name = "app.owner-id")
    String ownerId;

    @POST
    public Response reject(@HeaderParam("x-owner-id") String ownerHeader, @PathParam("id") Long id) {
        if (!isOwner(ownerHeader)) {
            throw new ActionForbiddenException("Ação não é permitida");
        }

        rejectWithdrawalRequestService.reject(id);
        return Response.noContent().build();
    }

    private boolean isOwner(String value) {
        return value != null
                && MessageDigest.isEqual(value.getBytes(StandardCharsets.UTF_8), ownerId.getBytes(StandardCharsets.UTF_8));
    }
}
