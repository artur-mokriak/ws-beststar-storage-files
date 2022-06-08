package com.beststar.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BestStarStorageFilesApplication {

	public static void main(String[] args) {
		System.out.println("Command-line arguments:");
		for(String arg:args) {
            System.out.println(arg);
        }
		System.out.println("Java System properties:");
		System.out.println(System.getProperties());
		SpringApplication.run(BestStarStorageFilesApplication.class, args);
	}
}
