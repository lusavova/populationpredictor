package com.example.populationpredictor.app.services;

import com.example.populationpredictor.app.models.PopulationInfo;
import com.example.populationpredictor.app.models.options.PagingOptions;
import com.example.populationpredictor.app.models.options.SortingOptions;
import com.example.populationpredictor.app.repositories.PopulationsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PopulationsServiceImpl implements PopulationService {
    private final PopulationsRepository repository;

    public PopulationsServiceImpl(PopulationsRepository repository) {
        this.repository = repository;
    }

    public Optional<PopulationInfo> getPopulationInfo(String country, int year) {
        return repository.getPopulationInfo(country, year);
    }

    public List<PopulationInfo> listPopulationInfos(
            int year, PagingOptions pagingOptions, SortingOptions sortingOptions) {
        return repository.listPopulationInfos(year, pagingOptions, sortingOptions);
    }

    @Override
    public void createPopulationInfo(List<PopulationInfo> populationInfoList) {
        repository.createPopulationInfo(populationInfoList);
    }
}
