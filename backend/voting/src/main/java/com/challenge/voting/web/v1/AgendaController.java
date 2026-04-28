package com.challenge.voting.web.v1;

import com.challenge.voting.application.dto.AgendaDetailsDTO;
import com.challenge.voting.application.dto.AgendaResponseDTO;
import com.challenge.voting.domain.Agenda;
import com.challenge.voting.repository.AgendaRepository;
import com.challenge.voting.service.AgendaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST controller for managing agendas.
 * Provides endpoints for creating, listing, and retrieving detailed information
 * about agendas.
 * All endpoints are under the /api/v1/agendas path.
 * 
 * @author Voting Challenge API
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/agendas")
@RequiredArgsConstructor
@Tag(name = "Agenda Management", description = "Endpoints for managing voting agendas")
public class AgendaController {

    private final AgendaRepository agendaRepository;
    private final AgendaService agendaService;

    @Schema(description = "Request payload for creating a new agenda")
    public record CreateAgendaRequest(
            @Schema(description = "The agenda title", example = "Q2 Budget Allocation", maxLength = 150) @NotBlank(message = "Title is required") String title,
            @Schema(description = "The agenda description", example = "Decision on budget allocation across departments for Q2 2024", maxLength = 500) @NotBlank(message = "Description is required") String description) {
    }

    /**
     * Creates a new agenda with the provided title and description.
     * 
     * @param request the agenda creation request containing title and description
     * @return AgendaResponseDTO containing the created agenda's details
     * @status 201 CREATED
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new voting agenda", description = """
            Creates a new agenda that can later have voting sessions opened for it.
            The agenda represents a proposal or topic that associates will vote on.

            **Business Rules:**
            - Title and description are required
            - Title maximum length: 150 characters
            - Description maximum length: 500 characters
            """, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateAgendaRequest.class), examples = @ExampleObject(name = "Create Budget Agenda", value = """
            {
              "title": "Q2 Budget Allocation",
              "description": "Decision on budget allocation across departments for Q2 2024"
            }
            """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Agenda created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgendaResponseDTO.class), examples = @ExampleObject(value = """
                    {
                      "id": 1,
                      "title": "Q2 Budget Allocation",
                      "description": "Decision on budget allocation across departments for Q2 2024"
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Validation error in request body", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-04-28T10:30:00Z",
                      "status": 400,
                      "error": "Bad Request",
                      "code": "VALIDATION_ERROR",
                      "message": "title: Title is required"
                    }
                    """)))
    })
    public AgendaResponseDTO create(@RequestBody @Valid CreateAgendaRequest request) {
        log.info("Creating new agenda with title: {}", request.title());

        Agenda agenda = Agenda.builder()
                .title(request.title())
                .description(request.description())
                .build();

        Agenda saved = agendaRepository.save(agenda);
        log.info("Agenda created successfully with ID: {}", saved.getId());

        return new AgendaResponseDTO(saved.getId(), saved.getTitle(), saved.getDescription());
    }

    /**
     * Retrieves all agendas from the system.
     * 
     * @return List of AgendaResponseDTO containing all agendas
     * @status 200 OK
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "List all agendas", description = """
            Retrieves a list of all agendas in the system, regardless of their voting status.
            This endpoint provides basic information about each agenda.

            **Note:** This endpoint does not include voting session details or results.
            Use GET /agendas/{id} for detailed information including voting results.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of agendas retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgendaResponseDTO[].class), examples = @ExampleObject(value = """
                    [
                      {
                        "id": 1,
                        "title": "Q2 Budget Allocation",
                        "description": "Decision on budget allocation across departments for Q2 2024"
                      },
                      {
                        "id": 2,
                        "title": "Policy Update 2024",
                        "description": "Annual policy review and updates for 2024"
                      }
                    ]
                    """)))
    })
    public List<AgendaResponseDTO> listAll() {
        log.debug("Listing all agendas");
        return agendaRepository.findAll().stream()
                .map(a -> new AgendaResponseDTO(a.getId(), a.getTitle(), a.getDescription()))
                .toList();
    }

    /**
     * Retrieves detailed information about a specific agenda including voting
     * session details.
     * Results are cached after a voting session is closed.
     * 
     * @param id the agenda ID
     * @return AgendaDetailsDTO containing comprehensive agenda information
     * @status 200 OK
     * @throws BusinessException if the agenda is not found (AGENDA_NOT_FOUND)
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get detailed agenda information", description = """
            Retrieves comprehensive information about a specific agenda, including:
            - Basic agenda details (title, description)
            - Current voting status (PENDING, OPEN, CLOSED)
            - Voting session information (if exists)
            - Vote counts and results (if session is closed)

            **Caching:** Results are cached after a voting session closes for improved performance.

            **Status Values:**
            - PENDING: No voting session has been opened
            - OPEN: Voting session is currently active
            - CLOSED: Voting session has ended, results are final
            """, parameters = {
            @Parameter(name = "id", description = "The unique identifier of the agenda", required = true, example = "1")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agenda details retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgendaDetailsDTO.class), examples = {
                    @ExampleObject(name = "Pending Agenda", value = """
                            {
                              "id": 1,
                              "title": "Q2 Budget Allocation",
                              "description": "Decision on budget allocation across departments for Q2 2024",
                              "status": "PENDING",
                              "sessionId": null,
                              "opensAt": null,
                              "closesAt": null,
                              "yesVotes": 0,
                              "noVotes": 0,
                              "totalVotes": 0
                            }
                            """),
                    @ExampleObject(name = "Open Agenda", value = """
                            {
                              "id": 1,
                              "title": "Q2 Budget Allocation",
                              "description": "Decision on budget allocation across departments for Q2 2024",
                              "status": "OPEN",
                              "sessionId": 1,
                              "opensAt": "2024-04-28T10:00:00Z",
                              "closesAt": "2024-04-28T10:05:00Z",
                              "yesVotes": 0,
                              "noVotes": 0,
                              "totalVotes": 0
                            }
                            """),
                    @ExampleObject(name = "Closed Agenda", value = """
                            {
                              "id": 1,
                              "title": "Q2 Budget Allocation",
                              "description": "Decision on budget allocation across departments for Q2 2024",
                              "status": "CLOSED",
                              "sessionId": 1,
                              "opensAt": "2024-04-28T10:00:00Z",
                              "closesAt": "2024-04-28T10:05:00Z",
                              "yesVotes": 45,
                              "noVotes": 12,
                              "totalVotes": 57
                            }
                            """)
            })),
            @ApiResponse(responseCode = "404", description = "Agenda not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "timestamp": "2024-04-28T10:30:00Z",
                      "status": 404,
                      "error": "Not Found",
                      "code": "AGENDA_NOT_FOUND",
                      "message": "Agenda not found"
                    }
                    """)))
    })
    public AgendaDetailsDTO getDetails(@PathVariable Long id) {
        log.debug("Retrieving details for agenda ID: {}", id);
        AgendaDetailsDTO details = agendaService.getAgendaDetails(id);
        log.debug("Successfully retrieved details for agenda ID: {} with status: {}", id, details.status());
        return details;
    }
}