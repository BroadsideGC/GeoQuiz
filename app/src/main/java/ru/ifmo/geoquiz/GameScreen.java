package ru.ifmo.geoquiz;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.games.Game;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import ru.ifmo.geoquiz.model.Round;
import ru.ifmo.geoquiz.model.Stage;

public class GameScreen extends FragmentActivity implements OnStreetViewPanoramaReadyCallback {

    private static final String LOG_TAG = "GeoSearch";

    private static final Integer RADIUS = 50_000;
    private static final Integer MAX_SEARCHING_TIME = 10_000;

    private Handler checkHandler = new Handler();
    private Round game;
    private Stage curStage;
    private StreetViewPanorama panorama = null;
    private LatLng prevPoint = null;
    private Boolean stopSearching = false;

    Button mapButton;
    MapDialog dialog;
    ProgressDialog progressSearching;
    CountDownTimer timerSearching;


    Bundle dialogArguments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        dialog = new MapDialog();
        if (savedInstanceState != null) {
            dialogArguments = savedInstanceState.getBundle("dialogArguments");
        }

        mapButton = (Button) findViewById(R.id.map);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogArguments == null) {
                    dialog.setOriginalCoordinates(curStage.getOriginalPoint());
                } else {
                    dialog.setArguments(dialogArguments);
                }
                dialog.show(getSupportFragmentManager(), "MapDialog");
            }
        });

        if (savedInstanceState == null) {
            game = getIntent().getExtras().getParcelable("game");
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
        if (panorama != null && panorama.getLocation() != null && panorama.getLocation().links != null) {
            outState.putParcelable("point", panorama.getLocation().position);
        }
        if (dialogArguments != null) {
            outState.putBundle("dialogArguments", dialogArguments);
        }
    }

    @Override
    public void onStreetViewPanoramaReady(final StreetViewPanorama panorama) {
        this.panorama = panorama;
        this.panorama.setStreetNamesEnabled(false);
        if (prevPoint != null) {
            if (progressSearching != null) {
                if (progressSearching.isShowing()) {
                    progressSearching.dismiss();
                }
            }
            panorama.setPosition(prevPoint);
            mapButton.setEnabled(true);
        } else {
            startNewStage();
        }
    }

    public void moveToRandomPoint() {
        if (stopSearching) {
            return;
        }

        mapButton.setEnabled(false);
        if (progressSearching == null) {
            progressSearching = panoWaitingDialog();
            progressSearching.show();
        }

        if (timerSearching == null) {
            timerSearching = new CountDownTimer(MAX_SEARCHING_TIME, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.d(LOG_TAG, "Tick...");
                }

                @Override
                public void onFinish() {
                    if (panorama.getLocation() == null) {
                        searchFail();
                    }
                }
            };
            Log.d(LOG_TAG, "Start searching...");
            timerSearching.start();
        }

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
                    mapButton.setEnabled(true);
                    if (progressSearching.isShowing()) {
                        progressSearching.dismiss();
                    }
                    if (timerSearching != null) {
                        timerSearching.cancel();
                    }
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
        dialogArguments = null;
        timerSearching = null;
        stopSearching = false;

        if (game.getStagesRemainingCount() == 0) {
            Intent intent = new Intent(this, EndGame.class);
            intent.putExtra("score", game.score());
            startActivity(intent);
        } else {
            curStage = game.nextStage();
            moveToRandomPoint();
        }
    }

    private ProgressDialog panoWaitingDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching location...");
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    private void searchFail() {
        stopSearching = true;
        if (progressSearching.isShowing()) {
            progressSearching.dismiss();
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Location not found!");
        alertDialogBuilder.setNeutralButton("Try another", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(GameScreen.this, ChooseMenu.class);
                startActivity(intent);
            }
        });
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.show();
    }
}