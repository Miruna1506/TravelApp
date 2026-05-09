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

    public List<PlaceRecommendation> findRestaurants(String destination, String area, Integer limit) {

        String query;

        if (area != null && !area.isBlank()) {
            query = "restaurants in " + area + ", " + destination;
        } else {
            query = "restaurants in " + destination;
        }

        Map<String, Object> requestBody = Map.of(
                "textQuery", query
        );

        Map<String, Object> response = webClient.post()
                .uri("/places:searchText")
                .header("Content-Type", "application/json")
                .header("X-Goog-Api-Key", apiKey)
                .header("X-Goog-FieldMask", "places.displayName,places.formattedAddress,places.rating,places.location")
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
            PlaceRecommendation recommendation = mapGooglePlaceToRecommendation(place);

            if (recommendation != null) {
                recommendations.add(recommendation);
            }
        }

        if (limit != null && limit > 0 && recommendations.size() > limit) {
            return recommendations.subList(0, limit);
        }

        return recommendations;
    }

    public PlaceRecommendation findPlace(String placeName, String destination) {

        Map<String, Object> requestBody = Map.of(
                "textQuery", placeName + " in " + destination
        );

        Map<String, Object> response = webClient.post()
                .uri("/places:searchText")
                .header("Content-Type", "application/json")
                .header("X-Goog-Api-Key", apiKey)
                .header("X-Goog-FieldMask", "places.displayName,places.formattedAddress,places.rating,places.location")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || response.get("places") == null) {
            return null;
        }

        List<Map<String, Object>> places = (List<Map<String, Object>>) response.get("places");

        if (places.isEmpty()) {
            return null;
        }

        Map<String, Object> place = places.get(0);

        return mapGooglePlaceToRecommendation(place);
    }

    private PlaceRecommendation mapGooglePlaceToRecommendation(Map<String, Object> place) {

        if (place == null) {
            return null;
        }

        Map<String, Object> displayName = (Map<String, Object>) place.get("displayName");

        String name = displayName != null
                ? (String) displayName.get("text")
                : "Unknown place";

        String address = (String) place.get("formattedAddress");

        Object ratingObject = place.get("rating");
        Double rating = ratingObject != null
                ? Double.valueOf(ratingObject.toString())
                : null;

        Map<String, Object> location = (Map<String, Object>) place.get("location");

        Double latitude = null;
        Double longitude = null;

        if (location != null) {
            Object latObject = location.get("latitude");
            Object lngObject = location.get("longitude");

            latitude = latObject != null
                    ? Double.valueOf(latObject.toString())
                    : null;

            longitude = lngObject != null
                    ? Double.valueOf(lngObject.toString())
                    : null;
        }

        return new PlaceRecommendation(name, address, rating, latitude, longitude);
    }
}