package com.landlordpro.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    private List<String> apartmentNames;

    public List<String> getApartmentNames() {
        return apartmentNames;
    }

    public void setApartmentNames(List<String> apartmentNames) {
        this.apartmentNames = apartmentNames;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register the JavaTimeModule
        objectMapper.findAndRegisterModules(); // Optionally, to find other modules
        return objectMapper;
    }
}
