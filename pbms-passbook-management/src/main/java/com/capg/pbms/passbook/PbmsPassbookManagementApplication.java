package com.capg.pbms.passbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class PbmsPassbookManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(PbmsPassbookManagementApplication.class, args);
	}
@Bean
public RestTemplate getAll()
{
return new RestTemplate();	
}
}
