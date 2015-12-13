package ru.ifmo.geoquiz.model;

import java.util.Random;
import android.os.Parcel;
import android.os.Parcelable;
import ru.ifmo.geoquiz.utils.GeoSearch;

public class Round implements Parcelable {

    public static final Integer MAX_STAGES = 10;

    private GeoSearch geoSearch;

    private final Integer stagesCount;
    private Stage[] stages;
    private Integer curStageIndex;
    private Integer score;
    private Country[] availableCountries;

    public Stage[] getStages() {
        return stages;
    }

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
            this.availableCountries = geoSearch.getAllCountries();
        }
    }

    public Round(int stagesCount) {
        this(stagesCount, new Country[]{});
    }

    public Stage nextStage() {
        curStageIndex++;
        this.stages[curStageIndex] = new Stage();
        this.stages[curStageIndex].setCountry(chooseCountry());
        return this.stages[curStageIndex];
    }

    public Stage getCurStage() {
        if (curStageIndex < 0) {
            return null;
        }
        return this.stages[curStageIndex];
    }

    public Integer score() {
        this.score = 0;
        for (Stage s: stages) {
            if (s != null) {
                this.score += s.score();
            }
        }
        return this.score;
    }

    private Country chooseCountry() {
        Integer randomIndex = Math.abs(new Random().nextInt()) % availableCountries.length;
        return availableCountries[randomIndex];
    }

    public void invalidateLastStage() {
        if (curStageIndex >= 0) {
            curStageIndex--;
        }
    }

    public Integer getStagesRemainingCount() {
        return stagesCount - curStageIndex - 1;
    }

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