package com.example.populationpredictor.app.repositories;

import com.example.populationpredictor.app.models.PopulationInfo;

import java.util.List;

public interface PopulationsRepository {
    void createPopulationInfo(String country, int year, long population);

    void createPopulationInfo(List<PopulationInfo> populationInfoList);

    int getLatestGeneratedPopulationYear();
}
