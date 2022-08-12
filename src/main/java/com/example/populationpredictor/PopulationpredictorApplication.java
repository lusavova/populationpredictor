package com.example.populationpredictor;

import com.example.populationpredictor.app.client.PopulationsAPIClient;
import com.example.populationpredictor.app.models.PopulationInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class PopulationpredictorApplication {

	public static void main(String[] args) {
		SpringApplication.run(PopulationpredictorApplication.class, args);
	}

}
