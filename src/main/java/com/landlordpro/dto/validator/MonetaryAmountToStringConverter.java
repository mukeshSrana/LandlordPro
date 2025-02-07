package com.landlordpro.dto.validator;

import javax.money.MonetaryAmount;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MonetaryAmountToStringConverter implements Converter<MonetaryAmount, String> {

    @Override
    public String convert(MonetaryAmount source) {
        if (source == null) {
            return ""; // Return an empty string if the source is null
        }
        return source.getNumber().toString(); // Convert the MonetaryAmount to string (BigDecimal)
    }
}

