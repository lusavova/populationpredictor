package com.example.populationpredictor;

import com.example.populationpredictor.app.client.PopulationsAPIClient;
import com.example.populationpredictor.app.models.PopulationInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class PopulationpredictorApplication {

	public static void main(String[] args) {
		PopulationsAPIClient populationsAPIClient = new PopulationsAPIClient();
		List<PopulationInfo> populationInfos = populationsAPIClient.getAllPopulationInfo();
		populationInfos.forEach(System.out::println);
//		SpringApplication.run(PopulationpredictorApplication.class, args);
	}

}
