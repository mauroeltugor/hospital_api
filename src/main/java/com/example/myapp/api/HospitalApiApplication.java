package com.example.myapp.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.example.myapp")
@EntityScan(basePackages = "com.example.myapp.entity")
@EnableJpaRepositories(basePackages = "com.example.myapp.repository")
public class HospitalApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalApiApplication.class, args);
    }

}
