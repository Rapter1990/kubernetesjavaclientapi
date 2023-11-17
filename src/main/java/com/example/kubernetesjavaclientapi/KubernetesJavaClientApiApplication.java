package com.example.kubernetesjavaclientapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class {@link KubernetesJavaClientApiApplication} for the Kubernetes Java Client API demo.
 */
@SpringBootApplication
public class KubernetesJavaClientApiApplication {

	/**
	 * The main method that starts the Spring Boot application.
	 *
	 * @param args Command-line arguments passed to the application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(KubernetesJavaClientApiApplication.class, args);
	}

}
