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
import android.widget.TextView;

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
    public static final String BUNDLE_KEY_DIALOG_ARGUMENTS = "dialogArguments";
    public static final String BUNDLE_KEY_GAME = "game";
    public static final String BUNDLE_KEY_POINT = "point";
    public static final String BUNDLE_KEY_SCORE = "score";

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
    TextView textViewScore;


    Bundle dialogArguments;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        dialog = new MapDialog();
        if (savedInstanceState != null) {
            dialogArguments = savedInstanceState.getBundle(BUNDLE_KEY_DIALOG_ARGUMENTS);
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
            game = getIntent().getExtras().getParcelable(BUNDLE_KEY_GAME);
        } else {
            game = (Round) savedInstanceState.get(BUNDLE_KEY_GAME);
            curStage = game.getCurStage();
            prevPoint = (LatLng) savedInstanceState.get(BUNDLE_KEY_POINT);
        }

        textViewScore = (TextView) findViewById(R.id.score);
        textViewScore.setText(String.format(getString(R.string.score), game.getStagesCount() - game.getStagesRemainingCount(), game.getStagesCount(), game.score()));
        StreetViewPanoramaFragment streetViewPanoramaFragment = (StreetViewPanoramaFragment) getFragmentManager().findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (panorama != null && panorama.getLocation() != null && panorama.getLocation().links != null) {
            outState.putParcelable(BUNDLE_KEY_POINT, panorama.getLocation().position);
        } else {
            game.invalidateLastStage();
        }

        if (dialogArguments != null) {
            outState.putBundle(BUNDLE_KEY_DIALOG_ARGUMENTS, dialogArguments);
        }

        outState.putParcelable(BUNDLE_KEY_GAME, game);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressSearching != null && progressSearching.isShowing()) {
            progressSearching.dismiss();
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
        } else {
            if (!progressSearching.isShowing()) {
                progressSearching.show();
            }
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
            intent.putExtra(BUNDLE_KEY_GAME, game);
            startActivity(intent);
            finish();
        } else {
            textViewScore.setText(String.format(getString(R.string.score), game.getStagesCount() - game.getStagesRemainingCount() + 1, game.getStagesCount(), game.score()));
            curStage = game.nextStage();
            moveToRandomPoint();
        }
    }

    private ProgressDialog panoWaitingDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.searching));
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    private void searchFail() {
        stopSearching = true;
        if (progressSearching != null && progressSearching.isShowing()) {
            progressSearching.dismiss();
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.location_not_found));
        alertDialogBuilder.setNeutralButton(getString(R.string.try_another), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(GameScreen.this, ChooseMenu.class);
                startActivity(intent);
            }
        });
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.show();
    }

    @Override
    public void onBackPressed() {
        openReturnDialog();
    }

    private void openReturnDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                GameScreen.this);
        quitDialog.setTitle(getString(R.string.Return));

        quitDialog.setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(GameScreen.this, ChooseMenu.class);
                startActivity(intent);
                finish();
            }
        });

        quitDialog.setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        quitDialog.show();
    }
}