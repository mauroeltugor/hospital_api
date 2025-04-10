package com.example.hospital_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.hospital_api.entity")
public class HospitalApiApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(HospitalApiApplication.class, args);
		System.err.println("hello word, all working well");
	}

}
