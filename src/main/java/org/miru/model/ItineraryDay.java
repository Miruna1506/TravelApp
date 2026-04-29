package org.miru.model;

import java.util.List;

public class ItineraryDay {

    private int dayNumber;
    private String title;
    private List<String> activities;

    public ItineraryDay() {
    }

    public ItineraryDay(int dayNumber, String title, List<String> activities) {
        this.dayNumber = dayNumber;
        this.title = title;
        this.activities = activities;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getActivities() {
        return activities;
    }

    public void setActivities(List<String> activities) {
        this.activities = activities;
    }
}