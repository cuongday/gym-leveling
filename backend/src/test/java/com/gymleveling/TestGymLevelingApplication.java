package com.gymleveling;

import org.springframework.boot.SpringApplication;

public class TestGymLevelingApplication {

	public static void main(String[] args) {
		SpringApplication.from(GymLevelingApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
