package com.Santino.Suscripciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SuscripcionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SuscripcionesApplication.class, args);
	}

}
