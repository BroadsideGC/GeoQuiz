package ru.ifmo.geoquiz.model;

import com.google.android.gms.maps.model.LatLng;

public class Stage {
    private LatLng originalPoint;
    private LatLng userPoint;
    private Integer pts;
    private Country country;

    public Stage() {

    }

    public Stage(LatLng originalPoint) {
        this.originalPoint = originalPoint;
    }

    public Stage(LatLng originalPoint, LatLng userPoint) {
        this.originalPoint = originalPoint;
        this.userPoint = userPoint;
    }

    public Integer score() {
        pts = 100;
        return pts;
    }

    public LatLng getUserPoint() {
        return userPoint;
    }

    public void setUserPoint(LatLng userPoint) {
        this.userPoint = userPoint;
    }

    public LatLng getOriginalPoint() {
        return originalPoint;
    }

    public void setOriginalPoint(LatLng originalPoint) {
        this.originalPoint = originalPoint;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}