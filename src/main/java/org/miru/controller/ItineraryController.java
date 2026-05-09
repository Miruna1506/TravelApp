package org.miru.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.miru.model.AppUser;
import org.miru.model.Itinerary;
import org.miru.model.ItineraryStats;
import org.miru.model.Preference;
import org.miru.repository.AppUserRepository;
import org.miru.repository.ItineraryRepository;
import org.miru.service.GeminiService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/itinerary")
public class ItineraryController {

    private final ItineraryRepository itineraryRepository;
    private final AppUserRepository appUserRepository;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    public ItineraryController(
            ItineraryRepository itineraryRepository,
            AppUserRepository appUserRepository,
            GeminiService geminiService
    ) {
        this.itineraryRepository = itineraryRepository;
        this.appUserRepository = appUserRepository;
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
    public Itinerary saveItinerary(@RequestBody Itinerary itinerary, Authentication authentication) {
        AppUser user = getCurrentUser(authentication);

        itinerary.setUser(user);

        return itineraryRepository.save(itinerary);
    }

    @GetMapping
    public List<Itinerary> getAllItineraries(Authentication authentication) {
        AppUser user = getCurrentUser(authentication);

        return itineraryRepository.findByUser(user);
    }

    @GetMapping("/latest")
    public Itinerary getLatestItinerary(Authentication authentication) {
        AppUser user = getCurrentUser(authentication);

        return itineraryRepository.findTopByUserOrderByIdDesc(user)
                .orElseThrow(() -> new RuntimeException("No itineraries found"));
    }

    @GetMapping("/stats")
    public ItineraryStats getItineraryStats(Authentication authentication) {
        AppUser user = getCurrentUser(authentication);

        List<Itinerary> itineraries = itineraryRepository.findByUser(user);

        long totalItineraries = itineraries.size();

        long uniqueDestinations = itineraries.stream()
                .map(Itinerary::getDestination)
                .filter(destination -> destination != null && !destination.isBlank())
                .map(String::toLowerCase)
                .collect(Collectors.toSet())
                .size();

        return new ItineraryStats(totalItineraries, uniqueDestinations);
    }

    @GetMapping("/search")
    public List<Itinerary> searchItineraries(
            @RequestParam String destination,
            Authentication authentication
    ) {
        AppUser user = getCurrentUser(authentication);

        return itineraryRepository.findByUserAndDestinationContainingIgnoreCase(user, destination);
    }

    @GetMapping("/{id}")
    public Itinerary getItineraryById(@PathVariable Long id, Authentication authentication) {
        AppUser user = getCurrentUser(authentication);

        return itineraryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Itinerary not found"));
    }

    @PutMapping("/{id}")
    public Itinerary updateItinerary(
            @PathVariable Long id,
            @RequestBody Itinerary updatedItinerary,
            Authentication authentication
    ) {
        AppUser user = getCurrentUser(authentication);

        Itinerary existingItinerary = itineraryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Itinerary not found"));

        existingItinerary.setDestination(updatedItinerary.getDestination());
        existingItinerary.setSummary(updatedItinerary.getSummary());

        existingItinerary.getDays().clear();
        existingItinerary.getDays().addAll(updatedItinerary.getDays());

        return itineraryRepository.save(existingItinerary);
    }

    @DeleteMapping("/{id}")
    public void deleteItinerary(@PathVariable Long id, Authentication authentication) {
        AppUser user = getCurrentUser(authentication);

        Itinerary itinerary = itineraryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Itinerary not found"));

        itineraryRepository.delete(itinerary);
    }

    private AppUser getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}