package ru.ifmo.geoquiz.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Random;

import ru.ifmo.geoquiz.utils.GeoSearch;

public class Country implements Parcelable {
    private String name;
    private String ISOCode;
    private LatLngBounds bounds;

    public Country(String name, String iso, LatLngBounds bounds) {
        this.name = name;
        this.ISOCode = iso;
        this.bounds = bounds;
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

    public LatLngBounds getBounds() {
        if (bounds == null) {
            bounds = GeoSearch.getInstance().getLatLngBounds(ISOCode);
        }
        return bounds;
    }

    public void setBounds(LatLngBounds bounds) {
        this.bounds = bounds;
    }

    public LatLng getRandomPointInCountry() {
        Double lngSpan = getBounds().northeast.longitude - getBounds().southwest.longitude;
        Double latSpan = getBounds().northeast.latitude - getBounds().southwest.latitude;
        Random random = new Random(System.currentTimeMillis());
        return new LatLng(getBounds().southwest.latitude + latSpan * random.nextDouble(), getBounds().southwest.longitude + lngSpan * random.nextDouble());
    };

    // Parceling
    public Country(Parcel in) {
        name = in.readString();
        ISOCode = in.readString();
        bounds = in.readParcelable(LatLngBounds.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(ISOCode);
        dest.writeParcelable(bounds, flags);
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
