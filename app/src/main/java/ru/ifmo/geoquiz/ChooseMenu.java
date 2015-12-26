package ru.ifmo.geoquiz;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ru.ifmo.geoquiz.model.Country;
import ru.ifmo.geoquiz.model.Round;
import ru.ifmo.geoquiz.utils.GeoSearch;

public class ChooseMenu extends AppCompatActivity {
    public static final String BUNDLE_KEY_NAMES = "names";
    public static final String BUNDLE_KEY_ISO_CODES = "isoCodes";
    public static final String BUNDLE_KEY_STATUS = "status";
    public static final String BUNDLE_KEY_STAGES = "stagesCount";
    private static String LOG_TAG = "ChooseMenu";

    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> isoCodes = new ArrayList<>();
    private int stagesCount;

    RecyclerView listView;
    TextView stagesInRoundCount;
    ProgressBar progressBar;
    RecyclerAdapter adapter;
    GetCountriesTask getCountriesTask;
    private Status status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_menu);

        listView = (RecyclerView) findViewById(R.id.listView);
        listView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        stagesInRoundCount = (TextView) findViewById(R.id.rounds_count);

        adapter = new RecyclerAdapter(this, names);
        listView.setAdapter(adapter);

        stagesCount = 1;
        stagesInRoundCount.setText(String.format(getString(R.string.stages_count), stagesCount));

        Button btnPlus = (Button) findViewById(R.id.button_plus);
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stagesCount = Math.min(stagesCount + 1, Round.MAX_STAGES);
                stagesInRoundCount.setText(String.format(getString(R.string.stages_count), stagesCount));
            }
        });

        Button btnMinus = (Button) findViewById(R.id.button_minus);
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stagesCount = Math.max(stagesCount - 1, 1);
                stagesInRoundCount.setText(String.format(getString(R.string.stages_count), stagesCount));
            }
        });

        if (savedInstanceState != null) {
            getCountriesTask = (GetCountriesTask) getLastCustomNonConfigurationInstance();
        }
        if (getCountriesTask == null) {
            getCountriesTask = new GetCountriesTask(this);
            getCountriesTask.execute();
        } else {
            getCountriesTask.attachActivity(this);
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return getCountriesTask;
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
                startGame(getAdapterPosition());
            }
        }
    }

    /*
     * Start game in selected country
     */
    private void startGame(int id) {
        Intent intent = new Intent(this, GameScreen.class);

        Round game;
        // All world or selected country
        if (isoCodes.get(id) != null) {
            game = new Round(stagesCount, new Country[]{GeoSearch.getInstance().getCountry(isoCodes.get(id))});
        } else {
            game = new Round(stagesCount, new Country[]{});
        }

        intent.putExtra(GameScreen.BUNDLE_KEY_GAME, game);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        names.clear();
        names.addAll(savedInstanceState.getStringArrayList(BUNDLE_KEY_NAMES));
        isoCodes = savedInstanceState.getStringArrayList(BUNDLE_KEY_ISO_CODES);
        status = (Status) savedInstanceState.getSerializable(BUNDLE_KEY_STATUS);
        stagesCount = savedInstanceState.getInt(BUNDLE_KEY_STAGES);
        stagesInRoundCount.setText(String.format(getString(R.string.stages_count), stagesCount));

        if (status == Status.DONE) {
            progressBar.setVisibility(View.GONE);
        }
        if (names.size() > 0) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(BUNDLE_KEY_NAMES, names);
        outState.putStringArrayList(BUNDLE_KEY_ISO_CODES, isoCodes);
        outState.putSerializable(BUNDLE_KEY_STATUS, status);
        outState.putInt(BUNDLE_KEY_STAGES, stagesCount);
    }

    /*
     * Get countries from DB and create list in async task
     */
    class GetCountriesTask extends AsyncTask<Void, Void, Country[]> {
        private Context appContext;
        private ChooseMenu activity;

        GetCountriesTask(ChooseMenu activity) {
            this.appContext = activity.getApplicationContext();
            this.activity = activity;
        }

        void attachActivity(ChooseMenu activity) {
            this.activity = activity;
            this.appContext = activity.getApplicationContext();
        }


        @Override
        protected Country[] doInBackground(Void... ignore) {
            activity.status = ChooseMenu.Status.DOWNLOADING;
            return GeoSearch.getInstance().getAllCountries();
        }

        @Override
        protected void onPostExecute(Country[] countries) {
            activity.names.clear();
            activity.isoCodes.clear();

            // Add "All world"
            activity.names.add(getString(R.string.all_world));
            activity.isoCodes.add(null);

            // Add countries from white list
            for (Country country : countries) {
                if (Round.preferredCountries.contains(country.getISOCode())) {
                    activity.names.add(country.getName());
                    activity.isoCodes.add(country.getISOCode());
                }
            }

            activity.adapter.notifyDataSetChanged();
            activity.status = ChooseMenu.Status.DONE;
            activity.progressBar.setVisibility(View.GONE);
        }

    }

    enum Status {
        DOWNLOADING,
        DONE;
    }
}
