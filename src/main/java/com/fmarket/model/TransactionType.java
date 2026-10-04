package com.fmarket.model;

public enum TransactionType {
    CREDIT(1),
    DEBIT(2);

    private final int code;

    TransactionType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static TransactionType fromCode(int code) {
        for (TransactionType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de transação inválido: " + code);
    }
}
