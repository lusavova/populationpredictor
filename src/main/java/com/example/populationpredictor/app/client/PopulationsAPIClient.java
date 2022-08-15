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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PopulationsAPIClient {

    private static final Set<String> IGNORE = Set.of("American Samoa", "Arab World", "Aruba", "Australia", "Bahamas, The", "Bermuda", "British Virgin Islands", "Brunei Darussalam", "Caribbean small states", "Cayman Islands", "Central Europe and the Baltics", "Curacao", "Early-demographic dividend", "East Asia & Pacific", "East Asia & Pacific (IDA & IBRD countries)", "East Asia & Pacific (excluding high income)", "Euro area", "Europe & Central Asia", "Europe & Central Asia (IDA & IBRD countries)", "Europe & Central Asia (excluding high income)", "European Union", "Faroe Islands", "Fiji", "Fragile and conflict affected situations", "French Polynesia", "Giam", "Heavily indebted poor countries (HIPC)", "High income", "IBRD only", "IDA & IBRD total", "IDA blend", "IDA only", "IDA total", "Kiribati", "Korea, Dem. People’s Rep.", "Korea, Rep.", "Kosovo", "Kyrgyz Republic", "Lao PDR", "Late-demographic dividend", "Latin America & Caribbean", "Latin America & Caribbean (excluding high income)", "Latin America & the Caribbean (IDA & IBRD countries)", "Least developed countries: UN classification", "Low & middle income", "Low income", "Lower middle income", "Marshall Islands", "Micronesia, Fed. Sts.", "Middle East & North Africa", "Middle East & North Africa (IDA & IBRD countries)", "Middle East & North Africa (excluding high income)", "Middle income", "Nauru", "Northern Mariana Islands", "North America", "New Caledonia", "OECD members", "Other small states", "Pacific island small states", "Palau", "Papua New Guinea", "Post-demographic dividend", "Pre-demographic dividend", "Sao Tome and Principe", "Sint Maarten (Dutch part)", "Slovak Republic", "Small states", "Solomon Islands", "South Asia", "South Asia (IDA & IBRD)", "St. Kitts and Nevis", "St. Lucia", "St. Martin (French part)", "St. Vincent and the Grenadines", "Sub-Saharan Africa", "Sub-Saharan Africa (IDA & IBRD countries)", "Sub-Saharan Africa (excluding high income)", "Turks and Caicos Islands", "Tuvalu", "Upper middle income", "Virgin Islands (U.S.)", "West Bank and Gaza", "World");

    public static final String POPULATION_API_URL = "https://datahub.io/core/population/r/population.json";

    public List<PopulationInfo> getAllPopulationInfo() {
        try {
            return fetchPopulationData().stream()
                    .filter(data -> !IGNORE.contains(data.name))
                    .map(data -> new PopulationInfo(UUID.randomUUID(), new Country(UUID.randomUUID(), data.name), data.year, data.population))
                    .collect(Collectors.toList());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<PopulationData> fetchPopulationData() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(POPULATION_API_URL))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Type listType = new TypeToken<ArrayList<PopulationData>>() {
        }.getType();
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
