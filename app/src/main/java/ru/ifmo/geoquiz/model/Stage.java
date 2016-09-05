package ru.ifmo.geoquiz.model;

import com.google.android.gms.maps.model.LatLng;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model "Stage".
 */
public class Stage implements Parcelable {
    /**
     * Point on map that we generated for user.
     */
    private LatLng originalPoint;
    /**
     * Point on map that user choose.
     */
    private LatLng userPoint;
    /**
     * Points for stage.
     */
    private Integer pts;
    /**
     * Country for current stage.
     */
    private Country country;

    /**
     * Constructor for Stage.
     */
    public Stage() {
        pts = 0;
    }

    /**
     * Constructor for Stage with given original point.
     * @param originalPoint original point
     */
    public Stage(LatLng originalPoint) {
        this.originalPoint = originalPoint;
        this.pts = 0;
    }

    /**
     * Constructor for Stage with given original point and user point.
     * //WTF?
     * @param originalPoint original point
     * @param userPoint user point
     */
    public Stage(LatLng originalPoint, LatLng userPoint) {
        this.originalPoint = originalPoint;
        this.userPoint = userPoint;
        this.pts = 0;
    }

    /**
     * Calculate points for current stage.
     * It uses distance between original and user points and some magic coefficients.
     * @return {@link Integer} final score for current stage
     */
    public Integer score() {
        if (userPoint == null) {
            return 0;
        }
        float[] res = new float[3];
        Location.distanceBetween(originalPoint.latitude, originalPoint.longitude, userPoint.latitude, userPoint.longitude, res);
        pts = Math.max(2550 - Math.round(res[0] / 1000), 0);
        return pts;
    }

    /**
     * Get user point.
     * @return {@link com.google.android.gms.maps.model.LatLng} user point
     */
    public LatLng getUserPoint() {
        return userPoint;
    }

    /**
     * Set user point.
     * @param userPoint user point
     */
    public void setUserPoint(LatLng userPoint) {
        this.userPoint = userPoint;
    }

    /**
     * Get original point.
     * @return {@link com.google.android.gms.maps.model.LatLng} original point
     */
    public LatLng getOriginalPoint() {
        return originalPoint;
    }

    /**
     * Set original point.
     * @param originalPoint original point
     */
    public void setOriginalPoint(LatLng originalPoint) {
        this.originalPoint = originalPoint;
    }

    /**
     * Get country for current stage.
     * @return {@link Country} current country
     */
    public Country getCountry() {
        return country;
    }

    /**
     * Set country for current stage.
     * @param country new country
     */
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
        dest.writeParcelable(originalPoint, flags);
        dest.writeParcelable(userPoint, flags);
        dest.writeInt(pts);
        dest.writeParcelable(country, flags);
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