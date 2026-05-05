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
                Generate a 3-day travel itinerary based on these preferences:
                Destination: %s
                Period: %s
                Budget: %s
                Interests: %s

                Return the itinerary as plain text.
                Use exactly this format:
                Day 1: ...
                Day 2: ...
                Day 3: ...
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
                .uri("/models/gemini-3-flash-preview:generateContent?key=" + apiKey)
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