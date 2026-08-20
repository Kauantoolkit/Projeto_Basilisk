package com.basilisk.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCpfValidator implements ConstraintValidator<ValidCpf, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        String digits = value.replaceAll("\\D", "");
        if (digits.length() != 11) {
            return false;
        }

        if (digits.chars().distinct().count() == 1) {
            return false;
        }

        int first = computeDigit(digits.substring(0, 9));
        int second = computeDigit(digits.substring(0, 9) + first);

        return digits.endsWith("" + first + second);
    }

    private int computeDigit(String base) {
        int sum = 0;
        for (int i = 0; i < base.length(); i++) {
            int weight = base.length() + 1 - i;
            sum += Character.getNumericValue(base.charAt(i)) * weight;
        }
        int rest = sum % 11;
        return rest < 2 ? 0 : 11 - rest;
    }
}