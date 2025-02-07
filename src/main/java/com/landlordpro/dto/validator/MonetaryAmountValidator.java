package com.landlordpro.dto.validator;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.CurrencyUnit;
import java.math.BigDecimal;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MonetaryAmountValidator implements ConstraintValidator<ValidMonetaryAmount, MonetaryAmount> {

    // Reference to the desired currency (NOK)
    private final CurrencyUnit validCurrency = Monetary.getCurrency("NOK");

    @Override
    public void initialize(ValidMonetaryAmount constraintAnnotation) {
        // Initialization (if needed)
    }

    @Override
    public boolean isValid(MonetaryAmount value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null values will be handled by @NotNull constraint
        }

        // Check if the currency is NOK (using CurrencyUnit)
        if (!validCurrency.equals(value.getCurrency())) {
            context.buildConstraintViolationWithTemplate("Invalid currency. Only NOK is allowed.")
                .addConstraintViolation();
            return false;
        }

        // Ensure the amount is greater than or equal to 0.01 NOK
        MonetaryAmount minAmount = Monetary.getDefaultAmountFactory()
            .setCurrency(validCurrency)
            .setNumber(new BigDecimal("0.01"))
            .create();
        if (value.isLessThan(minAmount)) {
            context.buildConstraintViolationWithTemplate("Amount must be greater than or equal to 0.01 NOK.")
                .addConstraintViolation();
            return false;
        }

        // Ensure the amount has no more than 2 decimal places
        BigDecimal bigDecimalAmount = value.getNumber().numberValue(BigDecimal.class);
        if (bigDecimalAmount.scale() > 2) {
            context.buildConstraintViolationWithTemplate("Amount must have no more than 2 decimal places.")
                .addConstraintViolation();
            return false;
        }

        // Ensure the amount has no more than 6 digits before the decimal
        if (bigDecimalAmount.precision() > 6) {
            context.buildConstraintViolationWithTemplate("Amount must have no more than 6 digits before the decimal.")
                .addConstraintViolation();
            return false;
        }

        return true;
    }
}
