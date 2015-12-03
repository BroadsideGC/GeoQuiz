package ru.ifmo.geoquiz;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import ru.ifmo.geoquiz.model.Country;
import ru.ifmo.geoquiz.model.Round;
import ru.ifmo.geoquiz.model.Stage;
import ru.ifmo.geoquiz.utils.GeoSearch;

public class GameScreen extends FragmentActivity implements OnStreetViewPanoramaReadyCallback {

    private static final String LOG_TAG = "GeoSearch";

    private static final Integer RADIUS = 50_000;

    private Handler checkHandler = new Handler();
    private Round game;
    private Stage curStage;
    private StreetViewPanorama panorama = null;

    Button actionButton;
    Button mapButton;
    MapDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        dialog = new MapDialog();

        actionButton = (Button) findViewById(R.id.action);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToRandomPoint();
            }
        });
        mapButton = (Button) findViewById(R.id.map);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setMarkerCoordinates(curStage.getOriginalPoint());
                dialog.show(getSupportFragmentManager(), "my_tag");
            }
        });

        /*
         * Тестовая игра
         */
        GeoSearch geo = GeoSearch.getInstance();
        Country Ukraine = geo.getCountry("UA");
        game = new Round(4, new Country[]{Ukraine});
        curStage = game.nextStage();

        StreetViewPanoramaFragment streetViewPanoramaFragment = (StreetViewPanoramaFragment) getFragmentManager().findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);


    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return super.onRetainCustomNonConfigurationInstance();
    }

    @Override
    public void onStreetViewPanoramaReady(final StreetViewPanorama panorama) {
        this.panorama = panorama;
        this.panorama.setStreetNamesEnabled(false);
        moveToRandomPoint();
    }

    public void moveToRandomPoint() {
        final LatLng rndPoint = curStage.getCountry().getRandomPointInCountry();
        panorama.setPosition(rndPoint, RADIUS);
        Log.d(LOG_TAG, "Pano set in " + curStage.getCountry().getISOCode() + " at " + rndPoint.toString());
        checkHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("Check...");
                if (panorama.getLocation() != null && panorama.getLocation().links != null) {
                    Log.d(LOG_TAG, "Pano found in " + rndPoint.toString() + ". Original point " + panorama.getLocation().position);
                    curStage.setOriginalPoint(panorama.getLocation().position);
                } else {
                    Log.d(LOG_TAG, "No panos found in " + rndPoint.toString());
                    moveToRandomPoint();
                }
            }
        }, 500);
    }


}