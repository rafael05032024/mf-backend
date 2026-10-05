package com.fmarket.dto;

import java.math.BigDecimal;
import java.util.List;

public record ListTransactionsResponseDTO(
        BigDecimal balance,
        List<TransactionDTO> transaction) {

    public record TransactionDTO(
            Long id,
            int type,
            BigDecimal value,
            String description) {
    }
}
