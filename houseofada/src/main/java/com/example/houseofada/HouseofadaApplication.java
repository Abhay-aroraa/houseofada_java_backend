package com.example.houseofada;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class HouseofadaApplication {

	public static void main(String[] args) {
		SpringApplication.run(HouseofadaApplication.class, args);
		System.out.print("abhayy");
	}

}
