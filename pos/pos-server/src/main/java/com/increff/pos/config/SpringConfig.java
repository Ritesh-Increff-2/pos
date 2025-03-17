package com.increff.pos.config;

import com.increff.invoice.InvoiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Configuration
@SpringBootApplication
@ComponentScan(basePackages = {"com.increff.pos","com.increff.invoice"})
@RestController

public class SpringConfig {

	@Autowired
	private RestTemplate restTemplate;

	public static void main(String[] args) {
		SpringApplication.run(SpringConfig.class, args);
	}

	@Bean
	public InvoiceClient invoiceClient() {
		return new InvoiceClient(restTemplate);
	}
}
