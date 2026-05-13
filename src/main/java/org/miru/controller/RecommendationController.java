package org.miru.controller;

import org.miru.model.AppUser;
import org.miru.model.Itinerary;
import org.miru.model.ItineraryDay;
import org.miru.model.PlaceRecommendation;
import org.miru.repository.AppUserRepository;
import org.miru.repository.ItineraryRepository;
import org.miru.service.GooglePlacesService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final GooglePlacesService googlePlacesService;
    private final ItineraryRepository itineraryRepository;
    private final AppUserRepository appUserRepository;

    public RecommendationController(
            GooglePlacesService googlePlacesService,
            ItineraryRepository itineraryRepository,
            AppUserRepository appUserRepository
    ) {
        this.googlePlacesService = googlePlacesService;
        this.itineraryRepository = itineraryRepository;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping("/restaurants")
    public List<PlaceRecommendation> getRestaurants(
            @RequestParam String destination,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) Integer limit
    ) {
        return googlePlacesService.findRestaurants(destination, area, limit);
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
            @PathVariable int dayNumber,
            Authentication authentication
    ) {
        AppUser user = getCurrentUser(authentication);

        Itinerary itinerary = itineraryRepository.findByIdAndUser(itineraryId, user)
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

    @GetMapping("/itinerary/{itineraryId}/day/{dayNumber}/restaurants")
    public List<PlaceRecommendation> getRestaurantsForItineraryDay(
            @PathVariable Long itineraryId,
            @PathVariable int dayNumber,
            @RequestParam(required = false) Integer limit,
            Authentication authentication
    ) {
        AppUser user = getCurrentUser(authentication);

        Itinerary itinerary = itineraryRepository.findByIdAndUser(itineraryId, user)
                .orElseThrow(() -> new RuntimeException("Itinerary not found"));

        ItineraryDay selectedDay = itinerary.getDays()
                .stream()
                .filter(day -> day.getDayNumber() == dayNumber)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Day not found"));

        List<PlaceRecommendation> result = new ArrayList<>();

        for (String area : selectedDay.getRestaurantAreas()) {
            List<PlaceRecommendation> restaurants =
                    googlePlacesService.findRestaurants(itinerary.getDestination(), area, limit);

            result.addAll(restaurants);
        }

        if (limit != null && limit > 0 && result.size() > limit) {
            return result.subList(0, limit);
        }

        return result;
    }

    private AppUser getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}