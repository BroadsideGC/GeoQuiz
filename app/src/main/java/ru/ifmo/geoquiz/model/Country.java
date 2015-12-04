package ru.ifmo.geoquiz.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Random;

public class Country implements Parcelable {
    private String name;
    private String ISOCode;
    private LatLngBounds boundaries;

    public Country(String name, String iso, LatLngBounds boundaries) {
        this.name = name;
        this.ISOCode = iso;
        this.boundaries = boundaries;
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

    // Parceling
    public Country(Parcel in) {
        name = in.readString();
        ISOCode = in.readString();
        boundaries = in.readParcelable(LatLngBounds.class.getClassLoader());
        //Log.i("fg", boundaries.toString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(ISOCode);
        boundaries.writeToParcel(dest, flags);
    }

    public static final Parcelable.Creator<Country> CREATOR = new Parcelable.Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel source) {
            return new Country(source);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };
}
