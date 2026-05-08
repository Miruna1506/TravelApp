package org.miru.service;

import org.miru.model.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com/v1beta")
            .build();

    public String generateItinerary(Preference preference) {
        String prompt = """
        Generate a detailed 3-day travel itinerary based on these preferences:
        Destination: %s
        Period: %s
        Budget: %s
        Interests: %s

        Return ONLY valid JSON.
        Do not include markdown.
        Do not include explanations outside the JSON.

        Use exactly this JSON structure:
        {
          "destination": "destination name",
          "summary": "A friendly overview of the trip, 2-3 sentences.",
          "days": [
            {
              "dayNumber": 1,
              "description": "Detailed itinerary text for Day 1.",
              "places": [
                "Real place name 1",
                "Real place name 2"
              ],
              "restaurantAreas": [
                "Real area or neighborhood 1"
              ]
            },
            {
              "dayNumber": 2,
              "description": "Detailed itinerary text for Day 2.",
              "places": [
                "Real place name 3",
                "Real place name 4"
              ],
              "restaurantAreas": [
                "Real area or neighborhood 2"
              ]
            },
            {
              "dayNumber": 3,
              "description": "Detailed itinerary text for Day 3.",
              "places": [
                "Real place name 5",
                "Real place name 6"
              ],
              "restaurantAreas": [
                "Real area or neighborhood 3"
              ]
            }
          ]
        }

        Rules:
        - Generate exactly 3 days.
        - Each day must have 3 to 5 concrete real places.
        - Places must be real landmarks, museums, neighborhoods, parks or attractions from the destination.
        - Restaurant areas must be real neighborhoods or areas where restaurants can be searched.
        - Do not put full sentences in places.
        - Do not invent ticket prices, exact opening hours or availability.
        - Keep the description detailed but not too long.
        """.formatted(
                preference.getDestination(),
                preference.getPeriod(),
                preference.getBudget(),
                preference.getInterests()
        );
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
        );

        Map<String, Object> response = webClient.post()
                .uri("/models/gemini-2.5-flash:generateContent?key=" + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || response.get("candidates") == null) {
            throw new RuntimeException("No response from Gemini");
        }

        List<Map<String, Object>> candidates =
                (List<Map<String, Object>>) response.get("candidates");

        Map<String, Object> content =
                (Map<String, Object>) candidates.get(0).get("content");

        List<Map<String, Object>> parts =
                (List<Map<String, Object>>) content.get("parts");

        return (String) parts.get(0).get("text");
    }
}