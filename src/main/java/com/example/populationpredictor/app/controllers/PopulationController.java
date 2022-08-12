package com.example.populationpredictor.app.controllers;

import com.example.populationpredictor.app.client.PopulationsAPIClient;
import com.example.populationpredictor.app.models.PopulationInfo;
import com.example.populationpredictor.app.services.PopulationService;
import com.example.populationpredictor.app.services.PopulationsPredictorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/api/population")
public class PopulationController {
    private final PopulationsAPIClient populationsAPIClient;

    private final PopulationsPredictorService populationsPredictorService;

    private final PopulationService populationService;

    public PopulationController(PopulationsAPIClient populationsAPIClient, PopulationsPredictorService populationsPredictorService, PopulationService populationsService, PopulationService populationService) {
        this.populationsAPIClient = populationsAPIClient;
        this.populationsPredictorService = populationsPredictorService;
        this.populationService = populationService;
    }

    @PostMapping(value = "/internal/generate")
    public void seedPopulation(@RequestParam(defaultValue = "20") int yearsToLookHead) {
        List<PopulationInfo> populationInfos = populationsAPIClient.getAllPopulationInfo();

        List<PopulationInfo> populationInfosWithPredictions =
                populationsPredictorService.predictPopulations(populationInfos, yearsToLookHead);

        for (int i = 0; i < populationInfosWithPredictions.size(); i += 10) {
            populationService.createPopulationInfo(populationInfosWithPredictions.subList(i, i += 10));
        }
    }
}
