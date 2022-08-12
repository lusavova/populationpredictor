package com.example.populationpredictor.app.services;

import com.example.populationpredictor.app.models.PopulationInfo;
import com.example.populationpredictor.app.repositories.PopulationsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PopulationsServiceImpl implements PopulationService {
    private final PopulationsRepository repository;

    public PopulationsServiceImpl(PopulationsRepository repository) {
        this.repository = repository;
    }

    public void createPopulationInfo(String country, int year, long population) {
        repository.createPopulationInfo(country, year, population);
    }

    @Override
    public void createPopulationInfo(List<PopulationInfo> populationInfoList) {
        repository.createPopulationInfo(populationInfoList);
    }

    @Override
    public int getLatestGeneratedPopulationYear() {
        return 0;
    }
}
