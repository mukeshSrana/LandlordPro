package com.landlordpro.dto.validator;

import java.math.BigDecimal;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToMonetaryAmountConverter implements Converter<String, MonetaryAmount> {

    private static final CurrencyUnit NOK = Monetary.getCurrency("NOK");

    @Override
    public MonetaryAmount convert(String source) {
        if (source == null || source.isEmpty()) {
            return null; // Return null if the source is empty or null
        }

        try {
            // Ensure that the value is in the correct format
            return Monetary.getDefaultAmountFactory()
                .setCurrency(NOK)
                .setNumber(new BigDecimal(source)) // Handle conversion to BigDecimal
                .create();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert string to MonetaryAmount", e);
        }
    }
}



