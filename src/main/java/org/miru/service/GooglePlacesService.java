package org.miru.service;

import org.miru.model.PlaceRecommendation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GooglePlacesService {

    @Value("${google.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://places.googleapis.com/v1")
            .build();

    public List<PlaceRecommendation> findRestaurants(String destination) {

        Map<String, Object> requestBody = Map.of(
                "textQuery", "restaurants in " + destination
        );

        Map<String, Object> response = webClient.post()
                .uri("/places:searchText")
                .header("Content-Type", "application/json")
                .header("X-Goog-Api-Key", apiKey)
                .header("X-Goog-FieldMask", "places.displayName,places.formattedAddress,places.rating")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        List<PlaceRecommendation> recommendations = new ArrayList<>();

        if (response == null || response.get("places") == null) {
            return recommendations;
        }

        List<Map<String, Object>> places = (List<Map<String, Object>>) response.get("places");

        for (Map<String, Object> place : places) {
            Map<String, Object> displayName = (Map<String, Object>) place.get("displayName");

            String name = displayName != null ? (String) displayName.get("text") : "Unknown place";
            String address = (String) place.get("formattedAddress");

            Object ratingObject = place.get("rating");
            Double rating = ratingObject != null ? Double.valueOf(ratingObject.toString()) : null;

            recommendations.add(new PlaceRecommendation(name, address, rating));
        }

        return recommendations;
    }
}