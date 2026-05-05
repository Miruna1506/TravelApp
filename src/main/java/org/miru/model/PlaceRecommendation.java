package org.miru.model;

public class PlaceRecommendation {

    private String name;
    private String address;
    private Double rating;

    public PlaceRecommendation() {
    }

    public PlaceRecommendation(String name, String address, Double rating) {
        this.name = name;
        this.address = address;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Double getRating() {
        return rating;
    }
}