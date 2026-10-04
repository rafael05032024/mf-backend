package com.fmarket.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TransactionTypeConverter implements AttributeConverter<TransactionType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TransactionType type) {
        return type == null ? null : type.getCode();
    }

    @Override
    public TransactionType convertToEntityAttribute(Integer code) {
        return code == null ? null : TransactionType.fromCode(code);
    }
}
