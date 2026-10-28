package com.housekarma.housekarma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HousekarmaApplication {

	public static void main(String[] args) {
		SpringApplication.run(HousekarmaApplication.class, args);
	}

}
