package org.miru.controller;

import org.miru.model.PlaceRecommendation;
import org.miru.service.GooglePlacesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final GooglePlacesService googlePlacesService;

    public RecommendationController(GooglePlacesService googlePlacesService) {
        this.googlePlacesService = googlePlacesService;
    }

    @GetMapping("/restaurants")
    public List<PlaceRecommendation> getRestaurants(@RequestParam String destination) {
        return googlePlacesService.findRestaurants(destination);
    }
}