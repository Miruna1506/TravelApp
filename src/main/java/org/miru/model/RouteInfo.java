package org.miru.model;

public class RouteInfo {

    private Integer distanceMeters;
    private Double distanceKm;
    private String duration;
    private Integer durationMinutes;
    private String encodedPolyline;

    public RouteInfo() {
    }

    public RouteInfo(Integer distanceMeters, Double distanceKm, String duration, Integer durationMinutes, String encodedPolyline) {
        this.distanceMeters = distanceMeters;
        this.distanceKm = distanceKm;
        this.duration = duration;
        this.durationMinutes = durationMinutes;
        this.encodedPolyline = encodedPolyline;
    }

    public Integer getDistanceMeters() {
        return distanceMeters;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public String getDuration() {
        return duration;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public String getEncodedPolyline() {
        return encodedPolyline;
    }
}