package com.example.tgbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TgBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(TgBotApplication.class, args);
	}

}
