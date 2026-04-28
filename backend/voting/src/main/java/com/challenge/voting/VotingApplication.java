package com.challenge.voting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Voting System API.
 * Bootstraps the Spring Boot application for the voting challenge recruitment project.
 * 
 * <p>Application features:
 * <ul>
 *   <li>REST API for managing voting agendas and sessions</li>
 *   <li>CPF validation integration</li>
 *   <li>Vote registration and result calculation</li>
 *   <li>Comprehensive error handling</li>
 *   <li>Swagger UI documentation</li>
 * </ul>
 * 
 * <p>Available API endpoints:
 * <ul>
 *   <li>POST /api/v1/agendas - Create new agenda</li>
 *   <li>GET /api/v1/agendas - List all agendas</li>
 *   <li>GET /api/v1/agendas/{id} - Get agenda details</li>
 *   <li>POST /api/v1/sessions/open - Open voting session</li>
 *   <li>POST /api/v1/sessions/{sessionId}/votes - Register vote</li>
 * </ul>
 * 
 * <p>Documentation available at: http://localhost:8080/swagger-ui.html
 * 
 * @author Voting Challenge API
 * @version 1.0.0
 */
@SpringBootApplication
public class VotingApplication {

	/**
	 * Application entry point.
	 * Starts the Spring Boot application context.
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(VotingApplication.class, args);
	}

}
