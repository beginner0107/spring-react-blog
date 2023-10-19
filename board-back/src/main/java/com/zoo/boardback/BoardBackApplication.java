package com.zoo.boardback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BoardBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardBackApplication.class, args);
	}

}
