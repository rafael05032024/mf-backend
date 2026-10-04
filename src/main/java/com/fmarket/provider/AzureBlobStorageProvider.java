package com.fmarket.provider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlobProperties;
import com.fmarket.dto.GetMidiaResponseDTO;
import com.fmarket.dto.StoreMidiaRequestDTO;
import com.fmarket.dto.StoreMidiaResponseDTO;

import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

@Startup
@ApplicationScoped
public class AzureBlobStorageProvider implements BlobStorageProvider {

    @ConfigProperty(name = "azure.storage.connection-string")
    String connectionString;

    @ConfigProperty(name = "azure.storage.container-name")
    String containerName;

    private BlobContainerClient containerClient;

    @PostConstruct
    void init() {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        containerClient = blobServiceClient.getBlobContainerClient(containerName);

        containerClient.createIfNotExists();
    }

    @Override
    public StoreMidiaResponseDTO store(StoreMidiaRequestDTO request) throws IOException {
        FileUpload file = request.file();
        InputStream is = Files.newInputStream(file.uploadedFile());
        BlobClient blobClient = containerClient.getBlobClient(request.midiaName());

        blobClient.upload(
                is,
                file.size(),
                true);

        blobClient.setHttpHeaders(
                new BlobHttpHeaders()
                        .setContentType(file.contentType()));

        return new StoreMidiaResponseDTO(blobClient.getBlobUrl());
    }

    @Override
    public GetMidiaResponseDTO getMidia(String blobName) {
        BlobClient blobClient = containerClient.getBlobClient(blobName);
        BlobProperties properties = blobClient.getProperties();

        return new GetMidiaResponseDTO(blobClient.openInputStream(), properties.getContentType());
    }
}
