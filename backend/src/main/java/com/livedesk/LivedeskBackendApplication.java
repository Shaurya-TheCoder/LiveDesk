package com.livedesk;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LivedeskBackendApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		for(DotenvEntry entry : dotenv.entries()){
			System.setProperty(entry.getKey(), entry.getValue());
		}
		SpringApplication.run(LivedeskBackendApplication.class, args);
	}

}
