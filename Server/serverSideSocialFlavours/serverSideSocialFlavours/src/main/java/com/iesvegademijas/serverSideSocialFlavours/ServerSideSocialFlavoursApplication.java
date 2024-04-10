package com.iesvegademijas.serverSideSocialFlavours;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class ServerSideSocialFlavoursApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerSideSocialFlavoursApplication.class, args);
	}

}
