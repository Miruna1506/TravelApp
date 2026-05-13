package org.miru.controller;

import org.miru.model.AppUser;
import org.miru.model.DayRouteInfo;
import org.miru.model.Itinerary;
import org.miru.model.ItineraryDay;
import org.miru.model.PlaceRecommendation;
import org.miru.model.RouteInfo;
import org.miru.repository.AppUserRepository;
import org.miru.repository.ItineraryRepository;
import org.miru.service.GooglePlacesService;
import org.miru.service.GoogleRoutesService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final ItineraryRepository itineraryRepository;
    private final AppUserRepository appUserRepository;
    private final GooglePlacesService googlePlacesService;
    private final GoogleRoutesService googleRoutesService;

    public RouteController(
            ItineraryRepository itineraryRepository,
            AppUserRepository appUserRepository,
            GooglePlacesService googlePlacesService,
            GoogleRoutesService googleRoutesService
    ) {
        this.itineraryRepository = itineraryRepository;
        this.appUserRepository = appUserRepository;
        this.googlePlacesService = googlePlacesService;
        this.googleRoutesService = googleRoutesService;
    }

    @GetMapping("/itinerary/{itineraryId}/day/{dayNumber}")
    public RouteInfo getRouteForItineraryDay(
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

        List<PlaceRecommendation> placesWithCoordinates = getPlacesWithCoordinates(
                selectedDay,
                itinerary.getDestination()
        );

        return googleRoutesService.calculateRoute(placesWithCoordinates);
    }

    @GetMapping("/itinerary/{itineraryId}")
    public List<DayRouteInfo> getRoutesForItinerary(
            @PathVariable Long itineraryId,
            Authentication authentication
    ) {
        AppUser user = getCurrentUser(authentication);

        Itinerary itinerary = itineraryRepository.findByIdAndUser(itineraryId, user)
                .orElseThrow(() -> new RuntimeException("Itinerary not found"));

        List<DayRouteInfo> routes = new ArrayList<>();

        for (ItineraryDay day : itinerary.getDays()) {
            List<PlaceRecommendation> placesWithCoordinates = getPlacesWithCoordinates(
                    day,
                    itinerary.getDestination()
            );

            if (placesWithCoordinates.size() >= 2) {
                RouteInfo routeInfo = googleRoutesService.calculateRoute(placesWithCoordinates);
                routes.add(new DayRouteInfo(day.getDayNumber(), routeInfo));
            }
        }

        return routes;
    }

    private List<PlaceRecommendation> getPlacesWithCoordinates(ItineraryDay day, String destination) {
        List<PlaceRecommendation> placesWithCoordinates = new ArrayList<>();

        for (String placeName : day.getPlaces()) {
            PlaceRecommendation place =
                    googlePlacesService.findPlace(placeName, destination);

            if (place != null && place.getLatitude() != null && place.getLongitude() != null) {
                placesWithCoordinates.add(place);
            }
        }

        return placesWithCoordinates;
    }

    private AppUser getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}