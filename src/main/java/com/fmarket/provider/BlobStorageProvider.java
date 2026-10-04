package com.fmarket.provider;

import com.fmarket.dto.StoreImageRequestDTO;
import com.fmarket.dto.StoreImageResponseDTO;

public interface BlobStorageProvider {

    StoreImageResponseDTO storeImage(StoreImageRequestDTO request);

    StoreImageResponseDTO storeVideo(StoreImageRequestDTO request);
}
