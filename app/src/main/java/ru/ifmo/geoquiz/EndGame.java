package ru.ifmo.geoquiz;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import ru.ifmo.geoquiz.model.Round;
import ru.ifmo.geoquiz.model.Stage;

public class EndGame extends AppCompatActivity {

    private GoogleMap map;
    private SupportMapFragment fragment;
    // Маркер настоящей точки
    MarkerOptions markerOriginal = new MarkerOptions();
    // Маркер пользовательской точки
    MarkerOptions markerUser = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);
        TextView score = (TextView) findViewById(R.id.textView);
        Round game = (Round) getIntent().getExtras().get(GameScreen.BUNDLE_KEY_GAME);
        score.setText(String.format(getString(R.string.your_score), game.score()));
        fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.endMap);
        fragment.setRetainInstance(true);
        if (fragment != null) {
            map = fragment.getMap();
        }
        for (Stage s : game.getStages()) {
            addMarkersGameOver(s);
        }

        //score.setText(String.format(getString(R.string.your_score), getIntent().getExtras().getInt(GameScreen.BUNDLE_KEY_GAME)));
    }

    private void addMarkersGameOver(Stage s) {
        map.addMarker(markerOriginal.position(s.getOriginalPoint()));
        map.addMarker(markerUser.position(s.getUserPoint()));

        PolylineOptions line = new PolylineOptions().add(s.getOriginalPoint()).add(s.getUserPoint()).color(Color.RED);
        map.addPolyline(line);
    }

    public void backToMenu(View view) {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
    }
}
