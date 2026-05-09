package org.miru.controller;

import org.miru.model.Itinerary;
import org.miru.model.ItineraryDay;
import org.miru.model.PlaceRecommendation;
import org.miru.repository.ItineraryRepository;
import org.miru.service.GooglePlacesService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final GooglePlacesService googlePlacesService;
    private final ItineraryRepository itineraryRepository;

    public RecommendationController(GooglePlacesService googlePlacesService, ItineraryRepository itineraryRepository) {
        this.googlePlacesService = googlePlacesService;
        this.itineraryRepository = itineraryRepository;
    }

    @GetMapping("/restaurants")
    public List<PlaceRecommendation> getRestaurants(
            @RequestParam String destination,
            @RequestParam(required = false) String area
    ) {
        return googlePlacesService.findRestaurants(destination, area);
    }

    @GetMapping("/place")
    public PlaceRecommendation getPlace(
            @RequestParam String destination,
            @RequestParam String place
    ) {
        return googlePlacesService.findPlace(place, destination);
    }

    @GetMapping("/itinerary/{itineraryId}/day/{dayNumber}/places")
    public List<PlaceRecommendation> getPlacesForItineraryDay(
            @PathVariable Long itineraryId,
            @PathVariable int dayNumber
    ) {
        Itinerary itinerary = itineraryRepository.findById(itineraryId)
                .orElseThrow(() -> new RuntimeException("Itinerary not found"));

        ItineraryDay selectedDay = itinerary.getDays()
                .stream()
                .filter(day -> day.getDayNumber() == dayNumber)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Day not found"));

        List<PlaceRecommendation> result = new ArrayList<>();

        for (String place : selectedDay.getPlaces()) {
            PlaceRecommendation recommendation =
                    googlePlacesService.findPlace(place, itinerary.getDestination());

            if (recommendation != null) {
                result.add(recommendation);
            }
        }

        return result;
    }
}