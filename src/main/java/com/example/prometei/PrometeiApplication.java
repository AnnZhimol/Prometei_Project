package com.example.prometei;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PrometeiApplication {
	public static void main(String[] args) {
		try {
			SpringApplication.run(PrometeiApplication.class, args);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
