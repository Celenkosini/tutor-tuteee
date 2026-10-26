package com.proximtecproduct.tutor_tuteee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TutorTuteeeApplication {

	public static void main(String[] args) {
		SpringApplication.run(TutorTuteeeApplication.class, args);
	}

}
