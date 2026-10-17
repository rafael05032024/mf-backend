package com.fmarket.resource;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fmarket.exception.ActionForbiddenException;
import com.fmarket.service.ApproveWithdrawalRequestService;

import jakarta.inject.Inject;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/api/wallet/withdrawals/{id}/approve")
public class ApproveWithdrawalRequestResource {

    @Inject
    ApproveWithdrawalRequestService approveWithdrawalRequestService;

    @ConfigProperty(name = "app.owner-id")
    String ownerId;

    @POST
    public Response approve(@HeaderParam("x-owner-id") String ownerHeader, @PathParam("id") Long id) {
        if (!isOwner(ownerHeader)) {
            throw new ActionForbiddenException("Ação não é permitida");
        }

        approveWithdrawalRequestService.approve(id);
        return Response.noContent().build();
    }

    private boolean isOwner(String value) {
        return value != null
                && MessageDigest.isEqual(value.getBytes(StandardCharsets.UTF_8), ownerId.getBytes(StandardCharsets.UTF_8));
    }
}
