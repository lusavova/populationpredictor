package com.example.populationpredictor.app.models;

import java.util.UUID;

public final class PopulationInfo {
    private final UUID id;
    private final Country country;
    private final int year;
    private final long population;

    public PopulationInfo(UUID id, Country country, int year, long population) {
        this.id = id;
        this.country = country;
        this.year = year;
        this.population = population;
    }

    public UUID getId() {
        return id;
    }

    public Country getCountry() {
        return country;
    }

    public int getYear() {
        return year;
    }

    public long getPopulation() {
        return population;
    }

    @Override
    public String toString() {
        return "PopulationInfo{" +
                "country='" + country.getName() + '\'' +
                ", year=" + year +
                ", population=" + population +
                '}';
    }
}
