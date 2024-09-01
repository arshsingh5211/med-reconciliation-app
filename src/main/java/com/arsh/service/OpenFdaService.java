package com.arsh.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OpenFdaService {

    private final ObjectMapper objectMapper;
    private final String BASE_RESOURCE_URL = "https://api.fda.gov/drug/label.json";

    @Autowired
    public OpenFdaService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<String> getMedicationInfo(String medicationName) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(BASE_RESOURCE_URL)
                .queryParam("search", "openfda.generic_name:" + medicationName)
                .queryParam("limit", "1");

        List<String> medicationDetails = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(uriBuilder.toUriString(), String.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                JsonNode resultsNode = objectMapper.readTree(responseEntity.getBody()).path("results");

                Optional.ofNullable(resultsNode)
                        .filter(JsonNode::isArray)
                        .filter(results -> results.size() > 0)
                        .map(results -> results.get(0))
                        .ifPresentOrElse(result -> {
                            medicationDetails.add("Purpose: " + getValueOrDefault(result.path("purpose")));
                            medicationDetails.add("Indications and Usage: " + getValueOrDefault(result.path("indications_and_usage")));
                            medicationDetails.add("Ask Doctor: " + getValueOrDefault(result.path("ask_doctor")));
                            medicationDetails.add("Ask Doctor or Pharmacist: " + getValueOrDefault(result.path("ask_doctor_or_pharmacist")));
                            medicationDetails.add("Stop Use: " + getValueOrDefault(result.path("stop_use")));
                            medicationDetails.add("Pregnancy or Breast Feeding: " + getValueOrDefault(result.path("pregnancy_or_breast_feeding")));
                            medicationDetails.add("Dosage and Administration: " + getValueOrDefault(result.path("dosage_and_administration")));

                            JsonNode openFdaNode = result.path("openfda");
                            medicationDetails.add("Brand Name: " + getValueOrDefault(openFdaNode.path("brand_name")));
                            medicationDetails.add("Generic Name: " + getValueOrDefault(openFdaNode.path("generic_name")));
                            medicationDetails.add("Route: " + getValueOrDefault(openFdaNode.path("route")));
                        }, () -> medicationDetails.add("Error: No results found for the given medication name."));

            } else {
                medicationDetails.add("Error: Received unexpected status code " + responseEntity.getStatusCode());
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            medicationDetails.add("Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            medicationDetails.add("Error: Failed to retrieve medication information.");
        }

        return medicationDetails;
    }

    private String getValueOrDefault(JsonNode node) {
        return Optional.ofNullable(node)
                .filter(n -> !n.isMissingNode() && !n.isNull())
                .map(n -> n.isArray() && n.size() > 0 ? n.get(0).asText("") : n.asText(""))
                .orElse("Information not available");
    }
}