package com.cirt.ctf;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.ZoneId;
import java.util.TimeZone;

@SpringBootApplication
public class CtfApplication {

	public static void main(String[] args) {
		SpringApplication.run(CtfApplication.class, args);
	}

	@PostConstruct
	public void init(){
		// Setting Spring Boot SetTimeZone
		ZoneId z = ZoneId.of( "Asia/Dhaka" ) ;
		TimeZone.setDefault(TimeZone.getTimeZone(z));
	}

}
