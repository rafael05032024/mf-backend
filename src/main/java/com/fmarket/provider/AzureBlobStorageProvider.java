package com.fmarket.provider;

import com.fmarket.dto.StoreImageRequestDTO;
import com.fmarket.dto.StoreImageResponseDTO;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Implementação provisória: ainda não envia nada ao Azure Blob Storage,
 * apenas devolve um link fake.
 */
@ApplicationScoped
public class AzureBlobStorageProvider implements BlobStorageProvider {

    private static final String FAKE_BASE_URL = "https://fake.blob.core.windows.net/fmarket";

    @Override
    public StoreImageResponseDTO storeImage(StoreImageRequestDTO request) {
        return new StoreImageResponseDTO(FAKE_BASE_URL + "/images/" + request.fileName());
    }

    @Override
    public StoreImageResponseDTO storeVideo(StoreImageRequestDTO request) {
        return new StoreImageResponseDTO(FAKE_BASE_URL + "/videos/" + request.fileName());
    }
}
