package org.miru.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.miru.model.Itinerary;
import org.miru.model.Preference;
import org.miru.repository.ItineraryRepository;
import org.miru.service.GeminiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itinerary")
public class ItineraryController {

    private final ItineraryRepository itineraryRepository;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    public ItineraryController(ItineraryRepository itineraryRepository, GeminiService geminiService) {
        this.itineraryRepository = itineraryRepository;
        this.geminiService = geminiService;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping("/generate")
    public Itinerary generateItinerary(@RequestBody Preference preference) {
        try {
            String aiResponse = geminiService.generateItinerary(preference);

            aiResponse = aiResponse
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            Itinerary itinerary = objectMapper.readValue(aiResponse, Itinerary.class);

            return itineraryRepository.save(itinerary);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate itinerary with Gemini: " + e.getMessage());
        }
    }

    @GetMapping
    public List<Itinerary> getAllItineraries() {
        return itineraryRepository.findAll();
    }

    @GetMapping("/{id}")
    public Itinerary getItineraryById(@PathVariable Long id) {
        return itineraryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Itinerary not found"));
    }
}