package com.fmarket.provider;

import com.fmarket.dto.FraudPreventionExpectedDetailsDTO;
import com.fmarket.dto.FraudPreventionRequestDTO;
import com.fmarket.dto.FraudPreventionResponseDTO;

public interface FraudPreventionProvider {

    FraudPreventionResponseDTO createVerificationSession(
            FraudPreventionRequestDTO request,
            FraudPreventionExpectedDetailsDTO expectedDetails);
}
