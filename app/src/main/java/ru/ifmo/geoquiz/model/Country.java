package ru.ifmo.geoquiz.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Random;

public class Country {
    private Integer id;
    private String name;
    private String ISOCode;
    private LatLngBounds boundaries;

    public Country() {

    }

    public Country(String name, String iso, LatLngBounds boundaries) {
        this.name = name;
        this.ISOCode = iso;
        this.boundaries = boundaries;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getISOCode() {
        return ISOCode;
    }

    public void setISOCode(String ISOCode) {
        this.ISOCode = ISOCode;
    }

    public LatLngBounds getBoundaries() {
        return boundaries;
    }

    public void setBoundaries(LatLngBounds boundaries) {
        this.boundaries = boundaries;
    }

    public LatLng getRandomPointInCountry() {
        Double lngSpan = boundaries.northeast.longitude - boundaries.southwest.longitude;
        Double latSpan = boundaries.northeast.latitude - boundaries.southwest.latitude;
        Random random = new Random(System.currentTimeMillis());
        return new LatLng(boundaries.southwest.latitude + latSpan * random.nextDouble(), boundaries.southwest.longitude + lngSpan * random.nextDouble());
    };
}
