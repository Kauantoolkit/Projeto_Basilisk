package com.basilisk.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidPhoneValidator implements ConstraintValidator<ValidPhone, String> {

    private static final String PHONE_PATTERN =
            "^(\\+55\\s?)?(\\(?\\d{2}\\)?\\s?)?\\d{4,5}[-\\s]?\\d{4}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return value.trim().matches(PHONE_PATTERN);
    }
}