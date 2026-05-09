package org.miru.model;

public class DayRouteInfo {

    private int dayNumber;
    private RouteInfo routeInfo;

    public DayRouteInfo() {
    }

    public DayRouteInfo(int dayNumber, RouteInfo routeInfo) {
        this.dayNumber = dayNumber;
        this.routeInfo = routeInfo;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public RouteInfo getRouteInfo() {
        return routeInfo;
    }
}