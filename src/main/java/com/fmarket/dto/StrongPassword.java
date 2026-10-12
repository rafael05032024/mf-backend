package com.fmarket.dto;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;

@Documented
@Constraint(validatedBy = StrongPassword.Validator.class)
@Target({ FIELD, PARAMETER, RECORD_COMPONENT })
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {

    String message() default "A senha deve ter no mínimo 8 caracteres, com ao menos uma letra, um número e um caractere especial";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<StrongPassword, String> {

        private static final int MIN_LENGTH = 8;

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) {
                return true; // presença é responsabilidade de @NotBlank
            }
            if (value.length() < MIN_LENGTH) {
                return false;
            }
            boolean letter = false;
            boolean digit = false;
            boolean special = false;
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if (Character.isLetter(c)) {
                    letter = true;
                } else if (Character.isDigit(c)) {
                    digit = true;
                } else if (!Character.isWhitespace(c)) {
                    special = true;
                }
            }
            return letter && digit && special;
        }
    }
}
