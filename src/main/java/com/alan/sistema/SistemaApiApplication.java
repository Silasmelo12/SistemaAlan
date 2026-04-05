package com.alan.sistema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.alan.sistema.client")
@EnableMongoAuditing
public class SistemaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaApiApplication.class, args);
	}
}
