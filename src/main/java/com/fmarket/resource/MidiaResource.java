package com.fmarket.resource;

import com.azure.core.annotation.PathParam;
import com.fmarket.dto.GetMidiaResponseDTO;
import com.fmarket.provider.BlobStorageProvider;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/midia")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MidiaResource {

    @Inject
    BlobStorageProvider blobStorageProvider;

    @GET
    @Path("/{q}")
    public Response getMidia(@PathParam("q") String q) {

        GetMidiaResponseDTO stream = blobStorageProvider.getMidia(q);

        return Response.ok(stream.stream())
                .type(stream.contentType())
                .header(
                        "Content-Disposition",
                        "inline")
                .build();
    }
}
