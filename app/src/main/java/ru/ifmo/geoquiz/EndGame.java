package ru.ifmo.geoquiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EndGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);
        TextView score = (TextView)findViewById(R.id.textView);
        score.setText(String.format(getString(R.string.your_score), getIntent().getExtras().getInt(GameScreen.BUNDLE_KEY_SCORE)));
    }

    public void backToMenu(View view){
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }
}
