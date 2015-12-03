package ru.ifmo.geoquiz;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.games.Game;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;


public class MapDialog extends DialogFragment {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.fragment_map_dialog, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
                    if (map != null && userCoordinates != null) {
                        map.addMarker(new MarkerOptions().position(originalCoordinates));
                        map.addMarker(new MarkerOptions().position(userCoordinates));
                        isStageEnd = true;
                        confirmAnswer.setText("Next");
                    }
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
                        map.addMarker(new MarkerOptions().position(userCoordinates));
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.map)).commit();
    }

    public GoogleMap getMap() {
        return map;
    }

    public SupportMapFragment getFragment() {
        return fragment;
    }

    public LatLng getOriginalCoordinates() {
        return originalCoordinates;
    }

    public void setOriginalCoordinates(LatLng originalCoordinates) {
        this.originalCoordinates = originalCoordinates;
    }

    public LatLng getUserCoordinates() {
        return userCoordinates;
    }

    public void setUserCoordinates(LatLng userCoordinates) {
        this.userCoordinates = userCoordinates;
    }

    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    private void reset() {
        isStageEnd = false;
        userCoordinates = null;
        originalCoordinates = null;
    }
}
