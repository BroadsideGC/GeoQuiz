package ru.ifmo.geoquiz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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
    // Original point
    MarkerOptions markerOriginal = new MarkerOptions();
    // User point
    MarkerOptions markerUser = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        Button btnBack = (Button) findViewById(R.id.back_to_menu);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                startActivity(intent);
                finish();
            }
        });

        Round game = (Round) getIntent().getExtras().get(GameScreen.BUNDLE_KEY_GAME);

        TextView score = (TextView) findViewById(R.id.final_score);
        score.setText(String.format(getString(R.string.your_score), game.score()));

        fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.endMap);
        fragment.setRetainInstance(true);

        if (fragment != null) {
            map = fragment.getMap();
        }
        for (Stage s : game.getStages()) {
            addMarkersGameOver(s);
        }
    }

    /*
     * Show markers for points
     */
    private void addMarkersGameOver(Stage s) {
        map.addMarker(markerOriginal.position(s.getOriginalPoint()));
        map.addMarker(markerUser.position(s.getUserPoint()));

        PolylineOptions line = new PolylineOptions().add(s.getOriginalPoint()).add(s.getUserPoint()).color(Color.RED);
        map.addPolyline(line);
    }

    @Override
    public void onBackPressed() { }
}
