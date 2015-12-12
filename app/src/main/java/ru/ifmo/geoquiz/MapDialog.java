package ru.ifmo.geoquiz;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


public class MapDialog extends DialogFragment {
    private static final String LOG_TAG = "MapDialog";
    public static final String BUNDLE_KEY_STATE = "state";
    public static final String BUNDLE_KEY_IS_STAGE_END = "isStageEnd";
    public static final String BUNDLE_KEY_ORIGINAL_COORDINATES = "originalCoordinates";
    public static final String BUNDLE_KEY_USER_COORDINATES = "userCoordinates";
    public static final String BUNDLE_KEY_CAMERA_POSITION = "cameraPosition";

    // Фрагмент с картой
    private SupportMapFragment fragment;
    // Карта
    private GoogleMap map;
    // Координаты точки, которую угадываем
    private LatLng originalCoordinates;
    // Координаты точки, которую ставим
    private LatLng userCoordinates;
    // Узнали ли истинную точку
    private Boolean isStageEnd = false;
    // Кнопка подтверждения ответа/перехода к следующему раунду
    Button confirmAnswer;
    // Игровой активити
    GameScreen gameScreen;
    // Маркер настоящей точки
    MarkerOptions markerOriginal = new MarkerOptions();
    // Маркер пользовательской точки
    MarkerOptions markerUser = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        gameScreen = (GameScreen) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.fragment_map_dialog, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            restoreMarkers(getArguments());
        } else {
            if (savedInstanceState != null) {
                restoreMarkers(savedInstanceState.getBundle(BUNDLE_KEY_STATE));
            }
        }

        confirmAnswer = (Button) getDialog().findViewById(R.id.confirm);
        confirmAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStageEnd) {
                    gameScreen.getCurStage().setUserPoint(userCoordinates);
                    Toast.makeText(getContext(), "Your score: " + gameScreen.getCurStage().score(), Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();
                    reset();
                    gameScreen.startNewStage();
                } else {
                    isStageEnd = true;
                    addMarkersGameOver();
                    confirmAnswer.setText("Next");
                }
            }
        });

        fragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        fragment.setRetainInstance(true);
        if (fragment != null) {
            map = fragment.getMap();
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (!isStageEnd) {
                        map.clear();
                        userCoordinates = latLng;
                        map.addMarker(markerUser.position(userCoordinates));
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);

        if (getArguments() != null) {
            restoreMarkers(getArguments());
        }
        if (isStageEnd) {
            confirmAnswer.setText("Next");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.map)).commit();
        } catch (IllegalStateException e) {
            //
        }
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(BUNDLE_KEY_STATE, getBundleState());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        gameScreen.dialogArguments = getBundleState();
        super.onDismiss(dialog);
    }

    public void setOriginalCoordinates(LatLng originalCoordinates) {
        this.originalCoordinates = originalCoordinates;
    }

    private void reset() {
        isStageEnd = false;
        userCoordinates = null;
        originalCoordinates = null;
    }

    private void restoreMarkers(Bundle savedState) {
        boolean isStageEnd = savedState.getBoolean(BUNDLE_KEY_IS_STAGE_END);
        LatLng originalCoordinates = savedState.getParcelable(BUNDLE_KEY_ORIGINAL_COORDINATES);
        LatLng userCoordinates = savedState.getParcelable(BUNDLE_KEY_USER_COORDINATES);
        CameraPosition cp = savedState.getParcelable(BUNDLE_KEY_CAMERA_POSITION);

        if (originalCoordinates != null) {
            this.originalCoordinates = originalCoordinates;
        }

        if (userCoordinates != null) {
            this.userCoordinates = userCoordinates;
        }

        if (cp != null) {
            if (map != null) {
                map.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
            }
        }

        // Конец игры
        if (isStageEnd) {
            this.isStageEnd = true;
            addMarkersGameOver();
            if (confirmAnswer != null) {
                confirmAnswer.setText("Next");
            }
        } else {
            if (map != null) {
                if (this.userCoordinates != null) {
                    map.addMarker(markerUser.position(this.userCoordinates));
                }
            }
        }
    }

    private void addMarkersGameOver() {
        if (originalCoordinates == null || userCoordinates == null || map == null) {
            Log.e(LOG_TAG, "Map or coordinates is null!");
        } else {
            map.addMarker(markerOriginal.position(originalCoordinates));
            map.addMarker(markerUser.position(userCoordinates));

            PolylineOptions line = new PolylineOptions().add(originalCoordinates).add(userCoordinates).color(Color.RED);
            map.addPolyline(line);
        }
    }

    private Bundle getBundleState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_KEY_ORIGINAL_COORDINATES, originalCoordinates);
        bundle.putParcelable(BUNDLE_KEY_USER_COORDINATES, userCoordinates);
        bundle.putParcelable(BUNDLE_KEY_CAMERA_POSITION, map.getCameraPosition());
        bundle.putBoolean(BUNDLE_KEY_IS_STAGE_END, isStageEnd);
        return bundle;
    }
}
