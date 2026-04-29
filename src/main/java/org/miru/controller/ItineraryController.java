package org.miru.controller;

import org.miru.model.Itinerary;
import org.miru.model.Preference;
import org.miru.repository.ItineraryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/itinerary")
public class ItineraryController {

    private final ItineraryRepository itineraryRepository;

    public ItineraryController(ItineraryRepository itineraryRepository) {
        this.itineraryRepository = itineraryRepository;
    }

    @PostMapping("/generate")
    public Itinerary generateItinerary(@RequestBody Preference preference) {

        String interests = preference.getInterests().toLowerCase();

        List<String> days = new ArrayList<>();

        days.add("Day 1: Arrival in " + preference.getDestination() + " + city center walk");

        if (interests.contains("history")) {
            days.add("Day 2: Visit museums, historical sites and old city landmarks");
        } else if (interests.contains("food")) {
            days.add("Day 2: Local food tour, traditional restaurants and street food experience");
        } else if (interests.contains("parks") || interests.contains("nature")) {
            days.add("Day 2: Parks, gardens and relaxing outdoor activities");
        } else {
            days.add("Day 2: Visit popular attractions based on tourist preferences");
        }

        if (interests.contains("food")) {
            days.add("Day 3: Local restaurants, markets and culinary experiences");
        } else if (interests.contains("shopping")) {
            days.add("Day 3: Shopping areas, local stores and souvenir markets");
        } else {
            days.add("Day 3: Relaxed exploration and free time");
        }

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