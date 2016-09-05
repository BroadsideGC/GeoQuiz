package ru.ifmo.geoquiz.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Random;

import ru.ifmo.geoquiz.utils.GeoSearch;

/**
 * Model "Country".
 */
public class Country implements Parcelable {
    /**
     * Country name
     */
    private String name;
    /**
     * Country ISO code
     * https://en.wikipedia.org/wiki/ISO_3166-1
     */
    private String ISOCode;
    /**
     * Country bounds
     */
    private LatLngBounds bounds;

    /**
     * Constructor for Country.
     * @param name country name
     * @param iso country code
     * @param bounds country bounds
     */
    public Country(String name, String iso, LatLngBounds bounds) {
        this.name = name;
        this.ISOCode = iso;
        this.bounds = bounds;
    }

    /**
     * Get country name.
     * @return {@link String} country name
     */
    public String getName() {
        return name;
    }

    /**
     * Set country name.
     * @param name country name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get two-letter country code.
     * @return {@link String} short country code
     */
    public String getISOCode() {
        return ISOCode;
    }

    /**
     * Set short country code.
     * @param ISOCode country code
     */
    public void setISOCode(String ISOCode) {
        this.ISOCode = ISOCode;
    }

    /**
     * Get country bounds.
     * @return {@link com.google.android.gms.maps.model.LatLngBounds} country bounds
     */
    public LatLngBounds getBounds() {
        if (bounds == null) {
            bounds = GeoSearch.getInstance().getLatLngBounds(ISOCode);
        }
        return bounds;
    }

    /**
     * Set country bounds.
     * @param bounds {@link com.google.android.gms.maps.model.LatLngBounds} country bounds
     */
    public void setBounds(LatLngBounds bounds) {
        this.bounds = bounds;
    }

    /**
     * Return random point in country bounds.
     * @return {@link com.google.android.gms.maps.model.LatLngBounds} random point
     */
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
