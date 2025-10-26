package com.example.AI_DATA;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AiDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiDataApplication.class, args);
	}

}
