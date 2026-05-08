package org.miru.service;

import org.miru.model.PlaceRecommendation;
import org.miru.model.RouteInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GoogleRoutesService {

    @Value("${google.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://routes.googleapis.com")
            .build();

    public RouteInfo calculateRoute(List<PlaceRecommendation> places) {

        if (places == null || places.size() < 2) {
            throw new RuntimeException("At least 2 places are required to calculate a route");
        }

        PlaceRecommendation origin = places.get(0);
        PlaceRecommendation destination = places.get(places.size() - 1);

        List<Map<String, Object>> intermediates = new ArrayList<>();

        for (int i = 1; i < places.size() - 1; i++) {
            PlaceRecommendation place = places.get(i);

            intermediates.add(
                    Map.of(
                            "location", Map.of(
                                    "latLng", Map.of(
                                            "latitude", place.getLatitude(),
                                            "longitude", place.getLongitude()
                                    )
                            )
                    )
            );
        }

        Map<String, Object> requestBody = Map.of(
                "origin", Map.of(
                        "location", Map.of(
                                "latLng", Map.of(
                                        "latitude", origin.getLatitude(),
                                        "longitude", origin.getLongitude()
                                )
                        )
                ),
                "destination", Map.of(
                        "location", Map.of(
                                "latLng", Map.of(
                                        "latitude", destination.getLatitude(),
                                        "longitude", destination.getLongitude()
                                )
                        )
                ),
                "intermediates", intermediates,
                "travelMode", "WALK"
        );

        Map<String, Object> response = webClient.post()
                .uri("/directions/v2:computeRoutes")
                .header("Content-Type", "application/json")
                .header("X-Goog-Api-Key", apiKey)
                .header("X-Goog-FieldMask", "routes.distanceMeters,routes.duration,routes.polyline.encodedPolyline")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || response.get("routes") == null) {
            throw new RuntimeException("No route found");
        }

        List<Map<String, Object>> routes =
                (List<Map<String, Object>>) response.get("routes");

        if (routes.isEmpty()) {
            throw new RuntimeException("No route found");
        }

        Map<String, Object> route = routes.get(0);

        Integer distanceMeters = route.get("distanceMeters") != null
                ? Integer.valueOf(route.get("distanceMeters").toString())
                : null;

        String duration = route.get("duration") != null
                ? route.get("duration").toString()
                : null;

        Map<String, Object> polyline =
                (Map<String, Object>) route.get("polyline");

        String encodedPolyline = polyline != null
                ? (String) polyline.get("encodedPolyline")
                : null;

        Double distanceKm = distanceMeters != null
                ? Math.round((distanceMeters / 1000.0) * 100.0) / 100.0
                : null;

        Integer durationMinutes = null;

        if (duration != null && duration.endsWith("s")) {
            String secondsText = duration.replace("s", "");
            int seconds = Integer.parseInt(secondsText);
            durationMinutes = (int) Math.ceil(seconds / 60.0);
        }

        return new RouteInfo(
                distanceMeters,
                distanceKm,
                duration,
                durationMinutes,
                encodedPolyline
        );
    }
}