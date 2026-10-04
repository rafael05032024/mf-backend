package com.fmarket.provider;

import java.io.IOException;

import com.fmarket.dto.GetMidiaResponseDTO;
import com.fmarket.dto.StoreMidiaRequestDTO;
import com.fmarket.dto.StoreMidiaResponseDTO;

public interface BlobStorageProvider {

    StoreMidiaResponseDTO store(StoreMidiaRequestDTO request) throws IOException;

    GetMidiaResponseDTO getMidia(String blobName);
}
