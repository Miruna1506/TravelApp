package org.miru.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.miru.model.Itinerary;
import org.miru.repository.ItineraryRepository;
import org.miru.model.Preference;
import org.miru.service.GeminiService;
import org.springframework.web.bind.annotation.*;
import org.miru.model.ItineraryStats;
import java.util.stream.Collectors;
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

            return objectMapper.readValue(aiResponse, Itinerary.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate itinerary with Gemini: " + e.getMessage());
        }
    }

    @PostMapping("/save")
    public Itinerary saveItinerary(@RequestBody Itinerary itinerary) {
        return itineraryRepository.save(itinerary);
    }

    @GetMapping
    public List<Itinerary> getAllItineraries() {
        return itineraryRepository.findAll();
    }
    @GetMapping("/latest")
    public Itinerary getLatestItinerary() {
        return itineraryRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new RuntimeException("No itineraries found"));
    }
    @GetMapping("/stats")
    public ItineraryStats getItineraryStats() {
        List<Itinerary> itineraries = itineraryRepository.findAll();

        long totalItineraries = itineraries.size();

        long uniqueDestinations = itineraries.stream()
                .map(Itinerary::getDestination)
                .filter(destination -> destination != null && !destination.isBlank())
                .map(String::toLowerCase)
                .collect(Collectors.toSet())
                .size();

        return new ItineraryStats(totalItineraries, uniqueDestinations);
    }
    @GetMapping("/{id}")
    public Itinerary getItineraryById(@PathVariable Long id) {
        return itineraryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Itinerary not found"));
    }
    @PutMapping("/{id}")
    public Itinerary updateItinerary(@PathVariable Long id, @RequestBody Itinerary updatedItinerary) {
        Itinerary existingItinerary = itineraryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Itinerary not found"));

        existingItinerary.setDestination(updatedItinerary.getDestination());
        existingItinerary.setSummary(updatedItinerary.getSummary());

        existingItinerary.getDays().clear();
        existingItinerary.getDays().addAll(updatedItinerary.getDays());

        return itineraryRepository.save(existingItinerary);
    }
    @DeleteMapping("/{id}")
    public void deleteItinerary(@PathVariable Long id) {
        itineraryRepository.deleteById(id);
    }
}