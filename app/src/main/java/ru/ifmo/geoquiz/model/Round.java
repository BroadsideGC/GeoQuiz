package ru.ifmo.geoquiz.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import android.os.Parcel;
import android.os.Parcelable;
import ru.ifmo.geoquiz.utils.GeoSearch;

/**
 * Model "Round".
 */
public class Round implements Parcelable {

    /**
     * Max stages count per game round.
     */
    public static final Integer MAX_STAGES = 10;
    /**
     * Available countries.
     */
    public static final List<String> preferredCountries = Arrays.asList("AU", "AT", "BE", "BR", "CA", "CH", "CZ", "DE", "ES", "FI", "LV", "LT", "FR", "GB", "GR", "HU", "IL", "IT", "JP", "NL", "NO", "PL", "SE", "TR", "UA", "US", "EE");

    private GeoSearch geoSearch;
    private final Integer stagesCount;
    private Stage[] stages;
    private Integer curStageIndex;
    private Integer score;
    private Country[] availableCountries;

    /**
     * Get game stages for current round.
     * @return list of stages
     */
    public Stage[] getStages() {
        return stages;
    }

    /**
     * Constructor for Round.
     * @param stagesCount max stages count
     * @param availableCountries list of available countries
     */
    public Round(int stagesCount, Country[] availableCountries) {
        geoSearch = GeoSearch.getInstance();

        stagesCount = Math.min(stagesCount, MAX_STAGES);
        this.stagesCount = stagesCount;
        this.stages = new Stage[stagesCount];
        this.curStageIndex = -1;
        this.score = 0;

        if (availableCountries.length > 0) {
            this.availableCountries = availableCountries;
        } else {
            ArrayList<Country> countries = new ArrayList<>();
            for (String code : preferredCountries) {
                countries.add(geoSearch.getCountry(code));
            }
            this.availableCountries = countries.toArray(new Country[countries.size()]);
        }
    }

    /**
     * Constructor for Round.
     * @param stagesCount max stages count
     */
    public Round(int stagesCount) {
        this(stagesCount, new Country[]{});
    }

    /**
     * Activate and return next game stage.
     * @return {@link Stage} next stage
     */
    public Stage nextStage() {
        curStageIndex++;
        if (this.stages[curStageIndex] == null) {
            this.stages[curStageIndex] = new Stage();
            this.stages[curStageIndex].setCountry(chooseCountry());
        }
        return this.stages[curStageIndex];
    }

    /**
     * Get current game stage.
     * @return {@link Stage} current stage
     */
    public Stage getCurStage() {
        if (curStageIndex < 0) {
            return null;
        }
        return this.stages[curStageIndex];
    }

    /**
     * Calculate and get game score.
     * @return {@link Integer} game score
     */
    public Integer score() {
        this.score = 0;
        for (Stage s: stages) {
            if (s != null) {
                this.score += s.score();
            }
        }
        return this.score;
    }

    /**
     * Choose random country from available countries list.
     * @return {@link Country} country
     */
    private Country chooseCountry() {
        Integer randomIndex = Math.abs(new Random().nextInt()) % availableCountries.length;
        return availableCountries[randomIndex];
    }

    /**
     * Invalidate last stage.
     * It just decrements stages counter.
     */
    public void invalidateLastStage() {
        if (curStageIndex >= 0) {
            curStageIndex--;
        }
    }

    /**
     * Get remaining stages count for current round.
     * @return {@link Integer} remaining stages count
     */
    public Integer getStagesRemainingCount() {
        return stagesCount - curStageIndex - 1;
    }

    /**
     * Get max stages count for current round.
     * @return {@link Integer} max stages count
     */
    public Integer getStagesCount() {
        return stagesCount;
    }

    // Parceling
    public Round(Parcel in) {
        stagesCount = in.readInt();
        stages = in.createTypedArray(Stage.CREATOR);
        curStageIndex = in.readInt();
        score = in.readInt();
        availableCountries = in.createTypedArray(Country.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(stagesCount);
        dest.writeTypedArray(stages, flags);
        dest.writeInt(curStageIndex);
        dest.writeInt(score);
        dest.writeTypedArray(availableCountries, flags);
    }

    public static final Parcelable.Creator<Round> CREATOR = new Parcelable.Creator<Round>() {
        @Override
        public Round createFromParcel(Parcel source) {
            return new Round(source);
        }

        @Override
        public Round[] newArray(int size) {
            return new Round[size];
        }
    };
}