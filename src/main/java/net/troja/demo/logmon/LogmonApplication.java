package net.troja.demo.logmon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableScheduling
@RestController
public class LogmonApplication {
	private static final Logger LOGGER = LogManager.getLogger(LogmonApplication.class);

	public LogmonApplication() {
		LOGGER.info("Starting up...");
	}

	@RequestMapping("/")
	public String home() {
		return "LogMon running";
	}

	public static void main(String[] args) {
		SpringApplication.run(LogmonApplication.class, args);
	}
}
