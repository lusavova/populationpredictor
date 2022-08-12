package com.example.populationpredictor.app.services;

import com.example.populationpredictor.app.models.Country;
import com.example.populationpredictor.app.models.PopulationInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PopulationsPredictorService {
    public List<PopulationInfo> predictPopulations(
            List<PopulationInfo> populationInfos, int yearsToLookAhead) {

        List<String> countries = getCountries(populationInfos);
        List<PopulationInfo> result = new ArrayList<>(populationInfos);

        for (String country : countries) {
            List<PopulationInfo> countryPopulationInfo = getPopulationInfoByCountry(country, populationInfos);
            double growthRate = getGrowthRate(yearsToLookAhead, countryPopulationInfo);

            PopulationInfo lastPopulationInfo = countryPopulationInfo.get(countryPopulationInfo.size() - 1);
            int latestYear = lastPopulationInfo.getYear();
            double latestPopulation = lastPopulationInfo.getPopulation();

            for (int i = 0; i < yearsToLookAhead; i++) {
                latestPopulation = latestPopulation * (1 + growthRate);
                result.add(new PopulationInfo(
                        UUID.randomUUID(),
                        new Country(UUID.randomUUID(), country),
                        latestYear + i + 1,
                        (long) latestPopulation));
            }
        }

        return result;
    }

    private double getGrowthRate(int yearsToLookAhead, List<PopulationInfo> infosByCountry) {
        int start = Math.max(0, infosByCountry.size() - yearsToLookAhead);
        double sum = 0;
        for (int i = start + 1; i < infosByCountry.size(); i++) {
            double prevYear = infosByCountry.get(i - 1).getPopulation();
            double currYear = infosByCountry.get(i).getPopulation();
            sum += (currYear - prevYear) / prevYear;
        }
        return sum / (double) (yearsToLookAhead - 1);
    }

    private List<String> getCountries(List<PopulationInfo> populationInfos) {
        return populationInfos.stream()
                .map(info -> info.getCountry().getName())
                .distinct()
                .collect(Collectors.toList());
    }

    private List<PopulationInfo> getPopulationInfoByCountry(String country, List<PopulationInfo> populationInfos) {
        return populationInfos.stream()
                .filter(info -> info.getCountry().getName().equals(country))
                .sorted(Comparator.comparingInt(PopulationInfo::getYear))
                .collect(Collectors.toList());
    }
}
