package ru.ifmo.geoquiz.model;

import java.util.Random;

import ru.ifmo.geoquiz.utils.GeoSearch;

public class Round {

    private static final Integer MAX_STAGES = 10;

    private GeoSearch geoSearch;

    private final Integer stagesCount;
    private Stage[] stages;
    private Integer curStageIndex;
    private Integer score;
    private Country[] availableCountries;

    public Round(int stagesCount, Country[] availableCountries) {
        geoSearch = GeoSearch.getInstance();

        stagesCount = Math.min(stagesCount, MAX_STAGES);
        this.stagesCount = stagesCount;
        this.stages = new Stage[stagesCount];
        this.curStageIndex = 0;

        if (availableCountries.length > 0) {
            this.availableCountries = availableCountries;
        } else {
            this.availableCountries = geoSearch.getAllCountries();
        }
    }

    public Round(int stagesCount) {
        this(stagesCount, new Country[] {});
    }

    public Stage nextStage() {
        this.stages[curStageIndex] = new Stage();
        this.stages[curStageIndex].setCountry(chooseCountry());
        curStageIndex++;
        return this.stages[curStageIndex - 1];
    }

    public Integer score() {
        this.score = 0;
        for (Stage s: stages) {
            this.score += s.score();
        }
        return this.score;
    }

    private Country chooseCountry() {
        Integer randomIndex = Math.abs(new Random().nextInt()) % availableCountries.length;
        return availableCountries[randomIndex];
    }


}