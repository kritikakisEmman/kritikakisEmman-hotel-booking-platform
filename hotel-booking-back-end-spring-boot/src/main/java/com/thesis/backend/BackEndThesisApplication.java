package com.thesis.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackEndThesisApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackEndThesisApplication.class, args);
	}

}
