package com.example.populationpredictor.app.controllers;

import com.example.populationpredictor.app.models.PopulationInfo;
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

import static com.example.populationpredictor.app.models.options.SortingOptions.Type.DESC;

@RestController
@RequestMapping("/v1/api/population")
public class PopulationController {

    private final PopulationService populationService;

    @Autowired
    public PopulationController(PopulationService populationService) {
        this.populationService = populationService;
    }

    @GetMapping(value = "/population")
    public ResponseEntity<PopulationInfo> getPopulation(@RequestParam String country, @RequestParam int year) {
        return populationService.getPopulationInfo(country, year)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/top")
    public List<PopulationInfo> topPopulations(@RequestParam int year) {
        return populationService.listPopulationInfos(
                year, new PagingOptions(0, 20),
                new SortingOptions("population", DESC));
    }
}
