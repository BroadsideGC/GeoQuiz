package ru.ifmo.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import ru.ifmo.geoquiz.model.Country;
import ru.ifmo.geoquiz.model.Round;
import ru.ifmo.geoquiz.utils.GeoSearch;

public class ChooseMenu extends Activity {
    private String[] names;
    private String[] isoCodes;
    private String LOG_TAG = "Choose";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_menu);
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d(LOG_TAG, "itemClick: position = " + position + ", id = "
                        + id);
                startGame((int)id);
            }
        });
        if (savedInstanceState == null) {
            Country[] countries = GeoSearch.getInstance().getAllCountries();
            names = new String[countries.length];
            isoCodes = new String[countries.length];
            for (int i = 0; i < countries.length; i++) {
                names[i] = countries[i].getName();
                isoCodes[i] = countries[i].getISOCode();
            }
        }else{
            names = savedInstanceState.getStringArray("countries");
        }
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names));
    }

    private void startGame(int id){
        Intent intent = new Intent(this, GameScreen.class);
        Round game = new Round(1, new Country[]{GeoSearch.getInstance().getCountry(isoCodes[id])});
        intent.putExtra("gaem", game);
        //Log.i(LOG_TAG);
        startActivity(intent);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray("countries", names);
        outState.putStringArray("isocodes", isoCodes);
    }
}
