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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/v1/api/internal")
public class InternalPopulationController {
    private final PopulationsAPIClient populationsAPIClient;
    private final PopulationsPredictorService populationsPredictorService;
    private final PopulationService populationService;

    public InternalPopulationController(PopulationsAPIClient populationsAPIClient,
                                        PopulationsPredictorService populationsPredictorService,
                                        PopulationService populationService) {
        this.populationsAPIClient = populationsAPIClient;
        this.populationsPredictorService = populationsPredictorService;
        this.populationService = populationService;
    }

    @PostMapping(value = "/generate")
    public void seedPopulation(@RequestParam(defaultValue = "20") int yearsToLookHead) {
        List<PopulationInfo> populationInfos = populationsAPIClient.getAllPopulationInfo();

        List<PopulationInfo> populationInfosWithPredictions =
                populationsPredictorService.predictPopulations(populationInfos, yearsToLookHead);
        List<List<PopulationInfo>> batches = getPopulationInfoInBatches(populationInfosWithPredictions, 10);
        batches.forEach(populationService::createPopulationInfo);
    }

    public List<List<PopulationInfo>> getPopulationInfoInBatches(List<PopulationInfo> populationInfos, int batchSize) {
        return IntStream.iterate(0, i -> i < populationInfos.size(), i -> i + batchSize)
                .mapToObj(i -> populationInfos.subList(i, Math.min(i + batchSize, populationInfos.size())))
                .collect(Collectors.toList());
    }
}
