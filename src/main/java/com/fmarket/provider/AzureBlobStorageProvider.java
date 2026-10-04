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
import com.fmarket.dto.StoreImageRequestDTO;
import com.fmarket.dto.StoreImageResponseDTO;
import com.fmarket.dto.StoreMidiaRequestDTO;
import com.fmarket.dto.StoreMidiaResponseDTO;

import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

@Startup
@ApplicationScoped
public class AzureBlobStorageProvider implements BlobStorageProvider {

    private static final String FAKE_BASE_URL = "https://fake.blob.core.windows.net/fmarket";

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
    public StoreImageResponseDTO storeImage(StoreImageRequestDTO request) {
        return new StoreImageResponseDTO(FAKE_BASE_URL + "/images/" + request.fileName());
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
    public StoreImageResponseDTO storeVideo(StoreImageRequestDTO request) {
        return new StoreImageResponseDTO(FAKE_BASE_URL + "/videos/" + request.fileName());
    }
}
