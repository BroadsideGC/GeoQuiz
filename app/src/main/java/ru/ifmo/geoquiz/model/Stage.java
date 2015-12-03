package ru.ifmo.geoquiz.model;

import com.google.android.gms.maps.model.LatLng;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class Stage implements Parcelable {
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
        this.pts = 0;
    }

    public Integer score() {
        if (userPoint == null) {
            return 0;
        }
        float[] res = new float[3];
        Location.distanceBetween(originalPoint.latitude, originalPoint.longitude, userPoint.latitude, userPoint.longitude, res);
        pts = Math.max(2550 - Math.round(res[0] / 1000), 0);
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

    // Parceling
    public Stage(Parcel in) {
        this.originalPoint = in.readParcelable(LatLng.class.getClassLoader());
        this.userPoint = in.readParcelable(LatLng.class.getClassLoader());
        this.pts = in.readInt();
        this.country = in.readParcelable(Country.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        this.originalPoint.writeToParcel(dest, flags);
        this.userPoint.writeToParcel(dest, flags);
        dest.writeInt(this.pts);
        this.country.writeToParcel(dest, flags);
    }

    public static final Parcelable.Creator<Stage> CREATOR = new Parcelable.Creator<Stage>() {
        @Override
        public Stage createFromParcel(Parcel source) {
            return new Stage(source);
        }

        @Override
        public Stage[] newArray(int size) {
            return new Stage[size];
        }
    };
}