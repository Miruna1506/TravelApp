package org.miru.controller;

import org.miru.model.Itinerary;
import org.miru.model.ItineraryDay;
import org.miru.model.PlaceRecommendation;
import org.miru.model.RouteInfo;
import org.miru.repository.ItineraryRepository;
import org.miru.service.GooglePlacesService;
import org.miru.service.GoogleRoutesService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final ItineraryRepository itineraryRepository;
    private final GooglePlacesService googlePlacesService;
    private final GoogleRoutesService googleRoutesService;

    public RouteController(
            ItineraryRepository itineraryRepository,
            GooglePlacesService googlePlacesService,
            GoogleRoutesService googleRoutesService
    ) {
        this.itineraryRepository = itineraryRepository;
        this.googlePlacesService = googlePlacesService;
        this.googleRoutesService = googleRoutesService;
    }

    @GetMapping("/itinerary/{itineraryId}/day/{dayNumber}")
    public RouteInfo getRouteForItineraryDay(
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

        List<PlaceRecommendation> placesWithCoordinates = new ArrayList<>();

        for (String placeName : selectedDay.getPlaces()) {
            PlaceRecommendation place =
                    googlePlacesService.findPlace(placeName, itinerary.getDestination());

            if (place != null && place.getLatitude() != null && place.getLongitude() != null) {
                placesWithCoordinates.add(place);
            }
        }

        return googleRoutesService.calculateRoute(placesWithCoordinates);
    }
}
