package io.github.wmjuguna.daraja;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.service.registry.ImportHttpServices;

import io.github.wmjuguna.daraja.integrations.DarajaApiClient;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@ImportHttpServices(group = "daraja", types = DarajaApiClient.class)
public class PaymentsApplication {

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return objectMapper;
	}

	public static void main(String[] args) {
		SpringApplication.run(PaymentsApplication.class, args);
	}

}
