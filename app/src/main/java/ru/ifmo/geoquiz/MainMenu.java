package ru.ifmo.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.games.Game;
import com.google.android.gms.maps.model.LatLng;

import ru.ifmo.geoquiz.model.Round;
import ru.ifmo.geoquiz.model.Stage;
import ru.ifmo.geoquiz.utils.GeoSearch;

public class MainMenu extends Activity {

    private static long backPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Button btnNewGame = (Button) findViewById(R.id.new_game);
        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ChooseMenu.class));
                finish();
            }
        });

        Button btnDemoGame = (Button) findViewById(R.id.demo_game);
        btnDemoGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // demo game
                Round demoGame = new Round(4);
                demoGame.getStages()[0] = new Stage(new LatLng(55.7520508, 37.6163349)); // Moscow
                demoGame.getStages()[1] = new Stage(new LatLng(-33.8547366, 151.2258688)); // Sydney
                demoGame.getStages()[2] = new Stage(new LatLng(51.4921451, -0.1929781)); // London
                demoGame.getStages()[3] = new Stage(new LatLng(43.71857, 10.407631)); // Pisa

                Intent intent = new Intent(getApplicationContext(), GameScreen.class);
                intent.putExtra(GameScreen.BUNDLE_KEY_GAME, demoGame);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.Exit),
                    Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }
}
