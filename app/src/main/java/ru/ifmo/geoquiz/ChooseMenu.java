package ru.ifmo.geoquiz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

import ru.ifmo.geoquiz.model.Country;
import ru.ifmo.geoquiz.model.Round;
import ru.ifmo.geoquiz.utils.GeoSearch;

public class ChooseMenu extends Activity {
    private ArrayList<String> names;
    private ArrayList<String> isoCodes;
    RecyclerView listView;
    RecyclerAdapter adapter;
    GetCountries getCountries;
    private static String LOG_TAG = "Choose";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_menu);
        listView = (RecyclerView) findViewById(R.id.listView);
        listView.setLayoutManager(new LinearLayoutManager(this));

        names = new ArrayList<>();
        isoCodes = new ArrayList<>();
        adapter = new RecyclerAdapter(this, names);
        listView.setAdapter(adapter);
        if (savedInstanceState != null) {
            getCountries = (GetCountries) getLastNonConfigurationInstance();
        }
        if (getCountries == null) {
            getCountries = new GetCountries(this);
            getCountries.execute();
        } else {
            getCountries.attachActivity(this);
            // Log.d(TAG,);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public Object onRetainNonConfigurationInstance() {
        return getCountries;
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        private final ArrayList<String> items;
        private final LayoutInflater li;

        private RecyclerAdapter(Context context, ArrayList<String> items) {
            li = LayoutInflater.from(context);
            this.items = items;
            setHasStableIds(true);
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(li.inflate(R.layout.item_list, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.firstLine.setText(items.get(position));
        }

        @Override
        public long getItemId(int position) {
            return items.get(position).hashCode();
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void swap(ArrayList<String> data) {
            items.clear();
            items.addAll(data);
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            final TextView firstLine;

            public ViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                firstLine = (TextView) itemView.findViewById(R.id.fist_line);
            }

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked " + getAdapterPosition());
                startGame(getAdapterPosition());
            }
        }
    }

    private void startGame(int id) {
        Intent intent = new Intent(this, GameScreen.class);
        Round game = new Round(1, new Country[]{GeoSearch.getInstance().getCountry(isoCodes.get(id))});
        intent.putExtra("game", game);
        startActivity(intent);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        names.clear();
        names.addAll(savedInstanceState.getStringArrayList("names"));
        isoCodes = savedInstanceState.getStringArrayList("isoCodes");
        if (names.size() > 0) {
            adapter.notifyDataSetChanged();
            Log.d(TAG, "Notifed Restored " + names.size());
        }
        Log.d(TAG, "Restored " + names.size());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("names", names);
        Log.d(TAG, "Fukkin saved " + names.size());
        outState.putStringArrayList("isoCodes", isoCodes);
    }

    class GetCountries extends AsyncTask<Void, Void, Country[]> {
        private Context appContext;
        private ChooseMenu activity;

        GetCountries(ChooseMenu activity) {
            this.appContext = activity.getApplicationContext();
            this.activity = activity;
        }

        void attachActivity(ChooseMenu activity) {
            this.activity = activity;
            this.appContext = activity.getApplicationContext();
            Log.d(TAG, "Attached");
        }


        @Override
        protected Country[] doInBackground(Void... ignore) {
            return GeoSearch.getInstance().getAllCountries();
        }

        @Override
        protected void onPostExecute(Country[] countries) {
            boolean[] valid = new boolean[countries.length];
            valid[8] = true;
            valid[9] = true;
            valid[12] = true;
            valid[22] = true;
            valid[27] = true;
            valid[28] = true;
            valid[30] = true;
            valid[35] = true;
            valid[39] = true;
            valid[40] = true;
            valid[41] = true;
            valid[50] = true;
            valid[52] = true;
            valid[55] = true;
            valid[57] = true;
            valid[64] = true;
            valid[71] = true;
            valid[77] = true;
            valid[78] = true;
            valid[79] = true;
            valid[82] = true;
            valid[87] = true;
            valid[97] = true;
            valid[117] = true;
            valid[118] = true;
            valid[120] = true;
            valid[127] = true;
            valid[151] = true;
            valid[162] = true;
            valid[166] = true;

            isoCodes.clear();
            ArrayList<String> lnames = new ArrayList<>();
            // ArrayList<String> lisoCodes= new ArrayList<>();
            for (int i = 0; i < countries.length; i++) {
                if (valid[i]) {
                    lnames.add(countries[i].getName());
                    isoCodes.add(countries[i].getISOCode());
                }
            }
            names.clear();
            names.addAll(lnames);
            Log.d(TAG, "Filled " + names.size());
            adapter.notifyDataSetChanged();
            Log.d(TAG, "Notifed");
            Log.d(TAG, "READY ");
        }

    }

    private String TAG = "Choose Menu";
}
