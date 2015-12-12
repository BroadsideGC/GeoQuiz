package ru.ifmo.geoquiz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.ifmo.geoquiz.model.Country;
import ru.ifmo.geoquiz.model.Round;
import ru.ifmo.geoquiz.utils.GeoSearch;

public class ChooseMenu extends Activity {
    public static final String BUNDLE_KEY_NAMES = "names";
    public static final String BUNDLE_KEY_ISO_CODES = "isoCodes";
    public static final String BUNDLE_KEY_STATUS = "status";
    public static final String BUNDLE_KEY_STAGES = "stages";
    private static String LOG_TAG = "ChooseMenu";
    private static String VIEW_TEXT = "Stages: ";

    private ArrayList<String> names;
    private ArrayList<String> isoCodes;
    private int stages;
    RecyclerView listView;
    TextView rcount;
    ProgressBar progressBar;
    RecyclerAdapter adapter;
    GetCountries getCountries;
    private Status status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_menu);
        listView = (RecyclerView) findViewById(R.id.listView);
        listView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        rcount = (TextView) findViewById(R.id.roundsCount);
        names = new ArrayList<>();
        isoCodes = new ArrayList<>();
        adapter = new RecyclerAdapter(this, names);
        listView.setAdapter(adapter);
        stages = 1;
        rcount.setText(VIEW_TEXT + stages);
        if (savedInstanceState != null) {
            getCountries = (GetCountries) getLastNonConfigurationInstance();
        }
        if (getCountries == null) {
            getCountries = new GetCountries(this);
            getCountries.execute();
        } else {
            getCountries.attachActivity(this);
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

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            final TextView firstLine;

            public ViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                firstLine = (TextView) itemView.findViewById(R.id.fist_line);
            }

            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Clicked " + getAdapterPosition());
                startGame(getAdapterPosition());
            }
        }
    }

    public void roundPlus(View v) {
        if (stages < Round.MAX_STAGES) {
            stages++;
        }
        rcount.setText(VIEW_TEXT + stages);
    }

    public void roundMinus(View v) {
        if (stages > 1) {
            stages--;
        }
        rcount.setText(VIEW_TEXT + stages);
    }

    private void startGame(int id) {
        Intent intent = new Intent(this, GameScreen.class);
        Round game = new Round(stages, new Country[]{GeoSearch.getInstance().getCountry(isoCodes.get(id))});
        intent.putExtra(GameScreen.BUNDLE_KEY_GAME, game);
        startActivity(intent);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        names.clear();
        names.addAll(savedInstanceState.getStringArrayList(BUNDLE_KEY_NAMES));
        isoCodes = savedInstanceState.getStringArrayList(BUNDLE_KEY_ISO_CODES);
        status = (Status) savedInstanceState.getSerializable(BUNDLE_KEY_STATUS);
        stages = savedInstanceState.getInt(BUNDLE_KEY_STAGES);
        rcount.setText(VIEW_TEXT + stages);

        if (status == Status.DONE) {
            progressBar.setVisibility(View.GONE);
        }
        if (names.size() > 0) {
            adapter.notifyDataSetChanged();
            Log.d(LOG_TAG, "Notifed Restored " + names.size());
        }
        Log.d(LOG_TAG, "Restored " + names.size());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "Fukkin saved " + names.size());
        outState.putStringArrayList(BUNDLE_KEY_NAMES, names);
        outState.putStringArrayList(BUNDLE_KEY_ISO_CODES, isoCodes);
        outState.putSerializable(BUNDLE_KEY_STATUS, status);
        outState.putInt(BUNDLE_KEY_STAGES, stages);
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
            Log.d(LOG_TAG, "Attached");
        }


        @Override
        protected Country[] doInBackground(Void... ignore) {
            activity.status = ChooseMenu.Status.DOWNLOADING;
            return GeoSearch.getInstance().getAllCountries();
        }

        @Override
        protected void onPostExecute(Country[] countries) {
            List<String> availableCountries = Arrays.asList("AU", "AT", "BE", "BR", "CA", "CH", "CZ", "DE", "ES", "FI", "LV", "LT", "FR", "GB", "GR", "HU", "IL", "IT", "JP", "NL", "NO", "PL", "SE", "TR", "UA", "US", "EE");

            activity.names.clear();
            activity.isoCodes.clear();
            for (Country country : countries) {
                if (availableCountries.contains(country.getISOCode())) {
                    activity.names.add(country.getName());
                    activity.isoCodes.add(country.getISOCode());
                }
            }

            Log.d(LOG_TAG, "Filled " + activity.names.size());
            activity.adapter.notifyDataSetChanged();
            Log.d(LOG_TAG, "Notifed " + names.size());
            activity.status = ChooseMenu.Status.DONE;
            progressBar.setVisibility(View.GONE);
            Log.d(LOG_TAG, "READY ");
        }

    }

    enum Status {
        DOWNLOADING,
        DONE;
    }
}
