package com.landlordpro.dto.validator;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

@Component
public class MonetaryAmountToStringConverter implements Formatter<String> {

    @Override
    public String print(String object, Locale locale) {
        return object != null ? object : "";
    }

    @Override
    public String parse(String text, Locale locale) throws ParseException {
        return text;
    }
}


