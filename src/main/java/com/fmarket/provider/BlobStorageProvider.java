package com.fmarket.provider;

import java.io.IOException;

import com.fmarket.dto.StoreImageRequestDTO;
import com.fmarket.dto.StoreImageResponseDTO;
import com.fmarket.dto.StoreMidiaRequestDTO;
import com.fmarket.dto.StoreMidiaResponseDTO;

public interface BlobStorageProvider {

    StoreImageResponseDTO storeImage(StoreImageRequestDTO request);

    StoreMidiaResponseDTO store(StoreMidiaRequestDTO request) throws IOException;

    StoreImageResponseDTO storeVideo(StoreImageRequestDTO request);
}
