package com.landlordpro.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/api/pincode")
public class PincodeController {

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/{postalCode}")
    public ResponseEntity<String> getCityByPincode(@PathVariable String postalCode) {
        String countryCode = "NO";
        String username = "landlordpro";

        String url = "http://api.geonames.org/postalCodeLookupJSON?postalcode=" + postalCode + "&country=" + countryCode + "&username=" + username;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode postalcodesNode = rootNode.get("postalcodes");

            if (postalcodesNode != null && postalcodesNode.isArray() && postalcodesNode.size() > 0) {
                String cityName = postalcodesNode.get(0).get("adminName2").asText(); // Extract city name
                return ResponseEntity.ok(cityName);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("City not found for postal code: " + postalCode);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

