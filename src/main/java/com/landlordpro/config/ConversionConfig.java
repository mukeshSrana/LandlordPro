package com.landlordpro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.DefaultFormattingConversionService;

import com.landlordpro.dto.validator.MultipartFileToByteArrayConverter;
import com.landlordpro.dto.validator.StringToMonetaryAmountConverter;

@Configuration
public class ConversionConfig {

    @Bean
    public DefaultFormattingConversionService conversionService() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
        conversionService.addConverter(new StringToMonetaryAmountConverter());
        conversionService.addConverter(new MultipartFileToByteArrayConverter());
        return conversionService;
    }
}

