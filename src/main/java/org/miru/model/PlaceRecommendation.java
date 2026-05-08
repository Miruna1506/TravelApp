package org.miru.model;

public class PlaceRecommendation {

    private String name;
    private String address;
    private Double rating;
    private Double latitude;
    private Double longitude;

    public PlaceRecommendation() {
    }

    public PlaceRecommendation(String name, String address, Double rating, Double latitude, Double longitude) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}