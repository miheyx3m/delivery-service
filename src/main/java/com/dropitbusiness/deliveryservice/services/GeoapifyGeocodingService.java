package com.dropitbusiness.deliveryservice.services;

import com.dropitbusiness.deliveryservice.data.AppData;
import com.dropitbusiness.deliveryservice.models.Address;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class GeoapifyGeocodingService {

    @Autowired
    private AppData appData;
    @Autowired
    private RestTemplate restTemplate;

    public Address resolveAddress(String searchTerm) {
        String apiUrl = "https://api.geoapify.com/v1/geocode/search?text=" + searchTerm + "&apiKey=" + AppData.GEOAPIFY_API_KEY;

        ResponseEntity<JsonNode> response = restTemplate.getForEntity(apiUrl, JsonNode.class);

        JsonNode firstResult = response.getBody().get("features").get(0);
        JsonNode properties = firstResult.get("properties");

        Address resolvedAddress = new Address();
        resolvedAddress.setStreet(properties.get("street").asText());
        resolvedAddress.setLine1(properties.get("housenumber").asText());
        resolvedAddress.setLine2(""); // No line2 information available from Geoapify
        resolvedAddress.setCountry(properties.get("country").asText());
        resolvedAddress.setPostcode(properties.get("postcode").asText());
        appData.getAddresses().add(resolvedAddress);
        log.info("new address {}", resolvedAddress);
        return resolvedAddress;
    }
}
