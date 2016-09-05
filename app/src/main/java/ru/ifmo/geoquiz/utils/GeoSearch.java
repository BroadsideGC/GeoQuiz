package ru.ifmo.geoquiz.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

import ru.ifmo.geoquiz.App;
import ru.ifmo.geoquiz.db.DatabaseHelper;
import ru.ifmo.geoquiz.model.Country;

/**
 * Class that contains operations with DB.
 */
public class GeoSearch {

    private static GeoSearch instance;
    private final Context context;

    public static final String LOG_TAG = "GeoSearch";

    private SQLiteDatabase db = null;
    private GeoSearch() {
        this.context = App.getContext();
        initDB();
    }

    private void initDB() {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getReadableDatabase();
    }

    /**
     * Return current instance of GeoSearch.
     * @return {@link GeoSearch} current instance
     */
    public static GeoSearch getInstance() {
        if (instance == null) {
            instance = new GeoSearch();
        }
        return instance;
    }

    /**
     * Get two letter country code by ID (in DB)
     * @param countryID country ID in database
     * @return {@link String} ISO-code
     */
    public String getISOByID(Integer countryID) {
        Cursor selectISOCode = db.query(DatabaseHelper.COUNTRIES_TABLE, new String[]{DatabaseHelper.COUNTRY_ISO_CODE_FIELD}, "_id = ?", new String[]{countryID.toString()}, null, null, null);
        if (selectISOCode.getCount() > 0) {
            selectISOCode.moveToNext();
            String iso = selectISOCode.getString(selectISOCode.getColumnIndex(DatabaseHelper.COUNTRY_ISO_CODE_FIELD));
            selectISOCode.close();
            return iso;
        } else {
            return null;
        }
    }

    /**
     * Get country bounds by ID
     * @param countryID country ID in database
     * @return {@link com.google.android.gms.maps.model.LatLngBounds} country bounds
     */
    public LatLngBounds getLatLngBounds(Integer countryID) {
        return getLatLngBounds(getISOByID(countryID));
    }

    /**
     * Get country bounds by two letter country code
     * @param isoCode two-letter country code
     * @return {@link com.google.android.gms.maps.model.LatLngBounds} country bounds
     */
    public LatLngBounds getLatLngBounds(String isoCode) {
        Cursor boundaries = db.query(DatabaseHelper.BOUNDARIES_TABLE, new String[]{DatabaseHelper.BOUNDARY_LATITUDE_FIELD, DatabaseHelper.BOUNDARY_LONGITUDE_FIELD}, DatabaseHelper.BOUNDARY_ISO_CODE_FIELD + " = ?", new String[]{isoCode}, null, null, null);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        while (boundaries.moveToNext()) {
            Double latitude = boundaries.getDouble(boundaries.getColumnIndex(DatabaseHelper.BOUNDARY_LATITUDE_FIELD));
            Double longitude = boundaries.getDouble(boundaries.getColumnIndex(DatabaseHelper.BOUNDARY_LONGITUDE_FIELD));
            builder.include(new LatLng(latitude, longitude));
        }
        boundaries.close();
        return builder.build();
    }

    /**
     * Get country by two letter country code
     * @param isoCode ISO-code
     * @return {@link Country} country
     */
    public Country getCountry(String isoCode) {
        Cursor selectCountry = db.query(DatabaseHelper.COUNTRIES_TABLE, new String[]{DatabaseHelper.COUNTRY_ADMIN_NAME_FIELD, DatabaseHelper.COUNTRY_ISO_CODE_FIELD}, DatabaseHelper.COUNTRY_ISO_CODE_FIELD + " = ?", new String[]{isoCode}, null, null, null);
        if (selectCountry.getCount() > 0) {
            selectCountry.moveToNext();
            String iso = selectCountry.getString(selectCountry.getColumnIndex(DatabaseHelper.COUNTRY_ISO_CODE_FIELD));
            String admin = selectCountry.getString(selectCountry.getColumnIndex(DatabaseHelper.COUNTRY_ADMIN_NAME_FIELD));
            LatLngBounds boundaries = getLatLngBounds(iso);
            selectCountry.close();
            return new Country(admin, iso, boundaries);
        } else {
            return null;
        }
    }

    /**
     * Get country by ID
     * @param countryID country ID
     * @return {@link Country} country
     */
    public Country getCountry(Integer countryID) {
        return getCountry(getISOByID(countryID));
    }

    /**
     * Get all countries from database.
     * @return array with countries
     */
    public Country[] getAllCountries() {
        ArrayList<Country> out = new ArrayList<>();
        Cursor countries = db.query(DatabaseHelper.COUNTRIES_TABLE, new String[]{DatabaseHelper.COUNTRY_ADMIN_NAME_FIELD, DatabaseHelper.COUNTRY_ISO_CODE_FIELD}, null, null, null, null, null);
        while (countries.moveToNext()) {
            String admin = countries.getString(countries.getColumnIndex(DatabaseHelper.COUNTRY_ADMIN_NAME_FIELD));
            String iso = countries.getString(countries.getColumnIndex(DatabaseHelper.COUNTRY_ISO_CODE_FIELD));
            out.add(new Country(admin, iso, null));
        }
        countries.close();
        return out.toArray(new Country[out.size()]);
    }


}
