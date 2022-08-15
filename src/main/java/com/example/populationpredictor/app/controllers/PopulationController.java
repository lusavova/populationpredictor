package com.example.populationpredictor.app.controllers;

import com.example.populationpredictor.app.mapper.PopulationData;
import com.example.populationpredictor.app.mapper.PopulationMapper;
import com.example.populationpredictor.app.models.options.PagingOptions;
import com.example.populationpredictor.app.models.options.SortingOptions;
import com.example.populationpredictor.app.services.PopulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.populationpredictor.app.models.options.SortingOptions.Type.DESC;

@RestController
@RequestMapping("/v1/api/population")
public class PopulationController {

    private final PopulationService populationService;
    private final PopulationMapper populationMapper;

    @Autowired
    public PopulationController(PopulationService populationService, PopulationMapper populationMapper) {
        this.populationService = populationService;
        this.populationMapper = populationMapper;
    }

    @GetMapping
    public ResponseEntity<PopulationData> getPopulation(@RequestParam String country, @RequestParam int year) {
        return populationService.getPopulationInfo(country, year)
                .map(populationMapper::map)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/top")
    public List<PopulationData> topPopulations(@RequestParam int year,
                                               @RequestParam(required = false, defaultValue = "20") int limit) {
        return populationService.listPopulationInfos(
                        year, new PagingOptions(0, limit),
                        new SortingOptions("population", DESC)).stream()
                .map(populationMapper::map)
                .collect(Collectors.toList());
    }
}
