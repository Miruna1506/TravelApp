package org.miru.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ItineraryDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int dayNumber;

    @Column(length = 3000)
    private String description;

    @ElementCollection
    @Column(length = 1000)
    private List<String> places = new ArrayList<>();

    @ElementCollection
    @Column(length = 1000)
    private List<String> restaurantAreas = new ArrayList<>();

    public ItineraryDay() {
    }

    public ItineraryDay(int dayNumber, String description, List<String> places, List<String> restaurantAreas) {
        this.dayNumber = dayNumber;
        this.description = description;
        this.places = places;
        this.restaurantAreas = restaurantAreas;
    }

    public Long getId() {
        return id;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPlaces() {
        return places;
    }

    public void setPlaces(List<String> places) {
        this.places = places;
    }

    public List<String> getRestaurantAreas() {
        return restaurantAreas;
    }

    public void setRestaurantAreas(List<String> restaurantAreas) {
        this.restaurantAreas = restaurantAreas;
    }
}