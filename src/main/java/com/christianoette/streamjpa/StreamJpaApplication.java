package com.christianoette.streamjpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@Slf4j
public class StreamJpaApplication {

	@EventListener(ApplicationReadyEvent.class)
	public void onStarted() {
		log.info("Go to http://localhost:8080");
	}

	public static void main(String[] args) {
		SpringApplication.run(StreamJpaApplication.class, args);
	}

}
