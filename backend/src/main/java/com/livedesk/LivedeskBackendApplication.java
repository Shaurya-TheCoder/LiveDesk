package com.livedesk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LivedeskBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LivedeskBackendApplication.class, args);
	}

}
