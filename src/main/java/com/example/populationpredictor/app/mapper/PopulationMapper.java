package com.example.populationpredictor.app.mapper;

import com.example.populationpredictor.app.models.PopulationInfo;
import org.springframework.stereotype.Component;

@Component
public class PopulationMapper {
    public PopulationData map(PopulationInfo populationInfo) {
        return new PopulationData(populationInfo.getCountry().getName(),
                populationInfo.getYear(),
                populationInfo.getPopulation());
    }
}
