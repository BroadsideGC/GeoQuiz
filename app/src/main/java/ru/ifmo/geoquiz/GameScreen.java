package ru.ifmo.geoquiz;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
    private LatLng prevPoint = null;

    Button actionButton;
    Button mapButton;
    MapDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        dialog = new MapDialog();
        dialog.setGameScreen(this);

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
                dialog.setOriginalCoordinates(curStage.getOriginalPoint());
                dialog.show(getSupportFragmentManager(), "MapDialog");
            }
        });

        if (savedInstanceState == null) {
            // Тестовая игра
            GeoSearch geo = GeoSearch.getInstance();
            Country Ukraine = geo.getCountry("UA");
            game = new Round(4, new Country[]{Ukraine});
        } else {
            game = (Round) savedInstanceState.get("game");
            curStage = game.getCurStage();
            prevPoint = (LatLng) savedInstanceState.get("point");
        }

        StreetViewPanoramaFragment streetViewPanoramaFragment = (StreetViewPanoramaFragment) getFragmentManager().findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("game", game);
        if (panorama.getLocation() != null && panorama.getLocation().links != null) {
            outState.putParcelable("point", panorama.getLocation().position);
        }
    }

    @Override
    public void onStreetViewPanoramaReady(final StreetViewPanorama panorama) {
        this.panorama = panorama;
        this.panorama.setStreetNamesEnabled(false);
        if (prevPoint != null) {
            panorama.setPosition(prevPoint);
        } else {
            startNewStage();
        }
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

    public Stage getCurStage() {
        return curStage;
    }

    public void startNewStage() {
        if (game.getStagesRemainingCount() == 0) {
            Toast.makeText(getApplicationContext(), "Game over. Your score: " + game.score(), Toast.LENGTH_LONG).show();
        } else {
            curStage = game.nextStage();
            moveToRandomPoint();
        }
    }
}