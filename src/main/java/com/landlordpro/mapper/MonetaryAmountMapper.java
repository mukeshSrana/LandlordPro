package com.landlordpro.mapper;

import java.math.BigDecimal;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MonetaryAmountMapper {

    CurrencyUnit validCurrency = Monetary.getCurrency("NOK");

    @Named("stringToMonetaryAmount")
    default MonetaryAmount stringToMonetaryAmount(String amount) {
        if (amount == null || amount.isEmpty()) {
            return null;
        }
        try {
            BigDecimal value = new BigDecimal(amount);
            return Monetary.getDefaultAmountFactory()
                .setCurrency(validCurrency.getCurrencyCode())
                .setNumber(value)
                .create();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format: " + amount, e);
        }
    }

    @Named("monetaryAmountToString")
    default String monetaryAmountToString(MonetaryAmount amount) {
        return (amount != null) ? amount.getNumber().toString() : null;
    }

    @Named("bigDecimalToMonetaryAmount")
    default MonetaryAmount bigDecimalToMonetaryAmount(BigDecimal amount) {
        if (amount == null) {
            return null;
        }
        return Monetary.getDefaultAmountFactory()
            .setCurrency(validCurrency.getCurrencyCode())
            .setNumber(amount)
            .create();
    }

    @Named("monetaryAmountToBigDecimal")
    default BigDecimal monetaryAmountToBigDecimal(MonetaryAmount amount) {
        return (amount != null) ? amount.getNumber().numberValue(BigDecimal.class) : null;
    }
}

