package com.actoon.actoon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling // scheduling annotation
@ComponentScan(basePackages = {"com.actoon.actoon"})
public class ActoonApplication {
//	static {
//		System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
//	}
	public static void main(String[] args) {
		SpringApplication.run(ActoonApplication.class, args);
	}
}
