package com.challenge.voting.infra.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Enhanced configuration for OpenAPI/Swagger documentation.
 * Provides comprehensive API metadata for interactive API documentation.
 *
 * <p>
 * Features:
 * <ul>
 * <li>Detailed API information with contact and license details</li>
 * <li>Multiple server configurations (local, development, production)</li>
 * <li>Organized endpoint tags for better navigation</li>
 * <li>Comprehensive descriptions and examples</li>
 * </ul>
 *
 * <p>
 * Access points:
 * <ul>
 * <li>Swagger UI: /swagger-ui.html</li>
 * <li>OpenAPI JSON: /v3/api-docs</li>
 * <li>OpenAPI YAML: /v3/api-docs.yaml</li>
 * </ul>
 *
 * @author Voting Challenge API
 * @version 1.0.0
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates the enhanced OpenAPI bean with comprehensive API information.
     * Includes detailed metadata, server configurations, and endpoint organization.
     *
     * @return OpenAPI object with complete API documentation configuration
     */
    @Bean
    OpenAPI votingChallengeOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .servers(createServers())
                .tags(createTags());
    }

    /**
     * Creates detailed API information including contact details and licensing.
     *
     * @return Info object with comprehensive API metadata
     */
    private Info createApiInfo() {
        return new Info()
                .title("Voting System API")
                .description("""
                        ## Voting System API

                        A comprehensive REST API for managing voting agendas and sessions in a corporate environment.
                        This API enables associates to participate in democratic decision-making processes through
                        structured voting sessions with comprehensive validation and result tracking.

                        ### Key Features
                        - **Agenda Management**: Create and manage voting proposals
                        - **Session Control**: Open time-bounded voting periods
                        - **Vote Registration**: Secure vote casting with CPF validation
                        - **Result Tracking**: Real-time and cached voting results
                        - **Audit Trail**: Complete logging of all voting activities

                        ### Business Rules
                        - Each agenda can have only one voting session
                        - Associates can vote only once per session
                        - CPF validation ensures voting eligibility
                        - Sessions have defined time windows
                        - Results are cached after session closure

                        ### Security
                        - CPF-based associate identification
                        - External CPF validation service integration
                        - Duplicate vote prevention
                        - Session time boundary enforcement

                        ### API Version
                        **v1.0.0** - Production Ready
                        """)
                .version("v1.0.0")
                .contact(new Contact()
                        .name("Voting System Team"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    /**
     * Creates server configurations for different environments.
     * Actually supports only local development.
     *
     * @return List of Server objects for different environments
     */
    private List<Server> createServers() {
        return List.of(
                new Server()
                        .url("http://localhost:8080")
                        .description("Local Development Server")
                        .description("Development environment running on local machine"));
    }

    /**
     * Creates tags to organize API endpoints into logical groups.
     * Improves navigation and discoverability in Swagger UI.
     *
     * @return List of Tag objects for endpoint organization
     */
    private List<Tag> createTags() {
        return List.of(
                new Tag()
                        .name("Agenda Management")
                        .description("""
                                Endpoints for managing voting agendas.

                                **Use Cases:**
                                - Create new voting proposals
                                - List all available agendas
                                - Retrieve detailed agenda information including voting results
                                """),

                new Tag()
                        .name("Voting Sessions")
                        .description("""
                                Endpoints for managing voting sessions and vote registration.

                                **Use Cases:**
                                - Open new voting sessions for agendas
                                - Register votes from associates
                                - Track voting session status and timing
                                """));
    }
}