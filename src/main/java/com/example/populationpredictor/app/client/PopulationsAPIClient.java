package com.example.populationpredictor.app.client;

import com.example.populationpredictor.app.models.Country;
import com.example.populationpredictor.app.models.PopulationInfo;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PopulationsAPIClient {

    public static final String POPULATION_API_URL = "https://datahub.io/core/population/r/population.json";

    public List<PopulationInfo> getAllPopulationInfo() {
        try {
            return fetchPopulationData().stream()
                    .map(data -> new PopulationInfo(UUID.randomUUID(), new Country(UUID.randomUUID(), data.name), data.year, data.population))
                    .collect(Collectors.toList());
        } catch (IOException | InterruptedException e) {
            // TODO: handle exception
            throw new RuntimeException(e);
        }
    }

    private List<PopulationData> fetchPopulationData() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(POPULATION_API_URL))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Type listType = new TypeToken<ArrayList<PopulationData>>() {}.getType();
        return new Gson().fromJson(response.body(), listType);
    }

    static class PopulationData {
        @SerializedName(value = "Country Name")
        public String name;

        @SerializedName(value = "Value")
        public Long population;

        @SerializedName(value = "Year")
        public Integer year;
    }
}
