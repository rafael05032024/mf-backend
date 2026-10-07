package com.fmarket.provider;

import com.fmarket.dto.GeneratePixQrCodeRequestDTO;
import com.fmarket.dto.GeneratePixQrCodeResponseDTO;

public interface PaymentGatewayProvider {

    GeneratePixQrCodeResponseDTO generatePixQrCode(GeneratePixQrCodeRequestDTO request);
}
