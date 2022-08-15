package com.example.populationpredictor.app.services;

import com.example.populationpredictor.app.models.PopulationInfo;
import com.example.populationpredictor.app.models.options.PagingOptions;
import com.example.populationpredictor.app.models.options.SortingOptions;

import java.util.List;
import java.util.Optional;

public interface PopulationService {

    Optional<PopulationInfo> getPopulationInfo(String country, int year);

    List<PopulationInfo> listPopulationInfos (int year, PagingOptions pagingOptions, SortingOptions sortingOptions);

    void createPopulationInfo(List<PopulationInfo> populationInfoList);
}
