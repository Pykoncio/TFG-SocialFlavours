package com.iesvegademijas.serverSideSocialFlavours;

import com.iesvegademijas.serverSideSocialFlavours.security.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class ServerSideSocialFlavoursApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerSideSocialFlavoursApplication.class, args);
	}

}
