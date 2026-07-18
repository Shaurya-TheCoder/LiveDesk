package com.livedesk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication(
		exclude = {DataSourceAutoConfiguration.class}
)
public class LivedeskBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LivedeskBackendApplication.class, args);
	}

}
