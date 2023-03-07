package com.jonathanmanes.pricetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@PropertySource("classpath:application.properties")
@EnableMongoRepositories
@EnableScheduling
public class PriceTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PriceTrackerApplication.class, args);
	}

}
