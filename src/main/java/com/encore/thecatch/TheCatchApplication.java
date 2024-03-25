package com.encore.thecatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
public class TheCatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(TheCatchApplication.class, args);
	}

}
