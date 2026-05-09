package org.miru.model;

public class ItineraryStats {

    private long totalItineraries;
    private long uniqueDestinations;

    public ItineraryStats() {
    }

    public ItineraryStats(long totalItineraries, long uniqueDestinations) {
        this.totalItineraries = totalItineraries;
        this.uniqueDestinations = uniqueDestinations;
    }

    public long getTotalItineraries() {
        return totalItineraries;
    }

    public long getUniqueDestinations() {
        return uniqueDestinations;
    }
}