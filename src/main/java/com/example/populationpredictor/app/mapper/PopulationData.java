package com.example.populationpredictor.app.mapper;

public class PopulationData {
    public final String country;
    public final int year;
    public final long population;

    public PopulationData(String country, int year, long population) {
        this.country = country;
        this.year = year;
        this.population = population;
    }
}
