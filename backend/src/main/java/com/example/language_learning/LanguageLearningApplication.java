package com.example.language_learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LanguageLearningApplication {

	public static void main(String[] args) {
		SpringApplication.run(LanguageLearningApplication.class, args);
	}

}
