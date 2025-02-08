package com.landlordpro.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class BigDecimalValidator implements ConstraintValidator<ValidAmount, BigDecimal> {

    @Override
    public void initialize(ValidAmount constraintAnnotation) {
        // Initialization (if needed)
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // You can handle nulls separately (e.g., via @NotNull)
        }

        // Check that the value has no more than 2 decimal places
        if (value.scale() > 2) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Amount must have no more than 2 decimal places.")
                .addConstraintViolation();
            return false;
        }

        // Check that the value has no more than 6 digits before the decimal point
        if (value.precision() - value.scale() > 6) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Amount must have no more than 6 digits before the decimal.")
                .addConstraintViolation();
            return false;
        }

        return true;
    }
}
