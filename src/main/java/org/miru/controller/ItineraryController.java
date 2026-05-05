package org.miru.controller;

import org.miru.model.Itinerary;
import org.miru.model.Preference;
import org.miru.repository.ItineraryRepository;
import org.miru.service.GeminiService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/itinerary")
public class ItineraryController {

    private final ItineraryRepository itineraryRepository;
    private final GeminiService geminiService;

    public ItineraryController(ItineraryRepository itineraryRepository, GeminiService geminiService) {
        this.itineraryRepository = itineraryRepository;
        this.geminiService = geminiService;
    }

    @PostMapping("/generate")
    public Itinerary generateItinerary(@RequestBody Preference preference) {

        String aiResponse = geminiService.generateItinerary(preference);

        List<String> days = Arrays.stream(aiResponse.split("\\n"))
                .filter(line -> !line.isBlank())
                .toList();

        Itinerary itinerary = new Itinerary();
        itinerary.setDestination(preference.getDestination());
        itinerary.setDays(days);

        return itineraryRepository.save(itinerary);
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