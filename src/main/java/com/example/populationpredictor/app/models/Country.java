package com.example.populationpredictor.app.models;

import java.util.UUID;

public class Country {
    private final UUID id;
    private final String name;

    public Country(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
