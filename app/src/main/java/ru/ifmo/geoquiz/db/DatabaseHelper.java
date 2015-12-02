package ru.ifmo.geoquiz.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = "GeoSearchDBHelper";
    private static final String DB_NAME = "geo.db";
    private static final Integer DB_VERSION = 1;
    private Context context = null;

    public static final String COUNTRIES_TABLE = "countries";
    public static final String COUNTRY_ISO_CODE_FIELD = "iso";
    public static final String COUNTRY_ADMIN_NAME_FIELD = "admin";

    public static final String BOUNDARIES_TABLE = "borders";
    public static final String BOUNDARY_ISO_CODE_FIELD = "iso_code";
    public static final String BOUNDARY_LATITUDE_FIELD = "lat";
    public static final String BOUNDARY_LONGITUDE_FIELD = "lng";


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        create();
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    public void create() {
        if (!check()) {
            this.getReadableDatabase();
            InputStream in = null;
            OutputStream out = null;
            try {
                in = context.getAssets().open(DB_NAME);
                out = new FileOutputStream(context.getDatabasePath(DB_NAME).getPath());
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                out.flush();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error while getting assets db!");
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, e.getMessage());
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, e.getMessage());
                    }
                }
            }
        }
    }

    public boolean check() {
        SQLiteDatabase db = null;
        try {
            db = SQLiteDatabase.openDatabase(context.getDatabasePath(DB_NAME).getPath(), null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        if (db != null) {
            db.close();
        }
        return db != null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}