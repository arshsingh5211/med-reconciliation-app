package com.arsh.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class OpenFdaService {

    private final ObjectMapper objectMapper;
    private final String BASE_RESOURCE_URL = "https://api.fda.gov/drug/label.json";

    @Autowired
    public OpenFdaService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonNode getMedicationInfo(String medicationName) {
        // First, try to search by generic name
        JsonNode response = searchMedication("openfda.generic_name", medicationName);
        if (isValidResponse(response)) {
            return response;
        }

        // If no results for generic name, try searching by brand name
        response = searchMedication("openfda.brand_name", medicationName);
        if (isValidResponse(response)) {
            return response;
        }

        // If neither search returns results, return an error message
        return objectMapper.createObjectNode().put("error", "No results found for the given medication name.");
    }

    private boolean isValidResponse(JsonNode response) {
        return response != null && response.has("results") && response.get("results").size() > 0;
    }


    private JsonNode searchMedication(String searchType, String medicationName) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(BASE_RESOURCE_URL)
                .queryParam("search", searchType + ":" + medicationName);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(uriBuilder.toUriString(), String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return objectMapper.readTree(responseEntity.getBody());
            } else {
                return objectMapper.createObjectNode().put("error", "Unexpected status code: " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            return objectMapper.createObjectNode().put("error", "Failed to retrieve medication information: " + e.getMessage());
        }
    }
}