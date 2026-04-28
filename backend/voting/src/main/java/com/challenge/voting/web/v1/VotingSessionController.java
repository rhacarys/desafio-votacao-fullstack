package com.challenge.voting.web.v1;

import com.challenge.voting.application.dto.VoteRequestDTO;
import com.challenge.voting.domain.VotingSession;
import com.challenge.voting.service.VoteService;
import com.challenge.voting.service.VotingSessionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;

/**
 * REST controller for managing voting sessions and votes.
 * Provides endpoints for opening voting sessions and registering votes.
 * All endpoints are under the /api/v1/sessions path.
 * 
 * @author Voting Challenge API
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@Tag(name = "Voting Sessions", description = "Endpoints for managing voting sessions and vote registration")
public class VotingSessionController {

    private final VotingSessionService sessionService;
    private final VoteService voteService;

    @Schema(description = "Request payload for opening a voting session")
    public record OpenSessionRequest(
            @Schema(description = "The ID of the agenda to open a session for", example = "1")
            Long agendaId,
            @Schema(description = "Duration of the session in minutes (optional, defaults to 1)", example = "5", minimum = "1")
            Integer durationInMinutes) {
    }

    @Schema(description = "Response containing voting session details")
    public record SessionResponse(
            @Schema(description = "The unique identifier of the voting session", example = "1")
            Long sessionId,
            @Schema(description = "The ID of the agenda this session belongs to", example = "1")
            Long agendaId,
            @Schema(description = "Timestamp when the session opens", example = "2024-04-28T10:00:00Z")
            Instant opensAt,
            @Schema(description = "Timestamp when the session closes", example = "2024-04-28T10:05:00Z")
            Instant closesAt) {
    }

    /**
     * Opens a new voting session for a specific agenda.
     * The session duration defaults to 1 minute if not specified or if the specified value is invalid.
     * Only one voting session can exist per agenda at a time.
     * 
     * @param request contains agendaId and optional durationInMinutes
     * @return SessionResponse with the session details including open and close times
     * @status 201 CREATED
     * @throws BusinessException if the agenda doesn't exist (AGENDA_NOT_FOUND)
     * @throws BusinessException if a session already exists for the agenda (SESSION_ALREADY_EXISTS)
     */
    @PostMapping("/open")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Open a new voting session",
            description = """
                Opens a new voting session for an existing agenda. Associates can vote during the session timeframe.

                **Business Rules:**
                - Agenda must exist
                - Only one session can exist per agenda
                - Session duration defaults to 1 minute if not specified
                - Session starts immediately when opened
                - Associates can only vote during the session timeframe

                **Duration Rules:**
                - If durationInMinutes is null or ≤ 0, defaults to 1 minute
                - Session opens immediately at current time
                - Session closes at: opensAt + durationInMinutes
                """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OpenSessionRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Open 5-minute session",
                                            value = """
                                            {
                                              "agendaId": 1,
                                              "durationInMinutes": 5
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Open default session",
                                            value = """
                                            {
                                              "agendaId": 2
                                            }
                                            """
                                    )
                            }
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Voting session opened successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SessionResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "sessionId": 1,
                                      "agendaId": 1,
                                      "opensAt": "2024-04-28T10:00:00Z",
                                      "closesAt": "2024-04-28T10:05:00Z"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Agenda not found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp": "2024-04-28T10:30:00Z",
                                      "status": 404,
                                      "error": "Not Found",
                                      "code": "AGENDA_NOT_FOUND",
                                      "message": "Agenda not found"
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Session already exists for this agenda",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "timestamp": "2024-04-28T10:30:00Z",
                                      "status": 422,
                                      "error": "Unprocessable Content",
                                      "code": "SESSION_ALREADY_EXISTS",
                                      "message": "A voting session is already open or has existed for this agenda."
                                    }
                                    """
                            )
                    )
            )
    })
    public SessionResponse openSession(@RequestBody OpenSessionRequest request) {
        log.info("Opening voting session for agenda ID: {} with duration: {} minutes", 
                request.agendaId(), request.durationInMinutes());
        
        VotingSession session = sessionService.openSession(request.agendaId(), request.durationInMinutes());
        
        log.info("Voting session opened successfully. Session ID: {}, opens at: {}, closes at: {}", 
                session.getId(), session.getOpensAt(), session.getClosesAt());
        
        return new SessionResponse(
                session.getId(),
                session.getAgenda().getId(),
                session.getOpensAt(),
                session.getClosesAt());
    }

    /**
     * Registers a vote from an associate in an active voting session.
     * Validates the CPF status before accepting the vote.
     * Prevents duplicate votes from the same associate in the same session.
     * 
     * @param sessionId the ID of the voting session
     * @param voteRequest contains the CPF and vote choice (YES or NO)
     * @status 202 ACCEPTED
     * @throws BusinessException if the CPF is invalid (CPF_INVALID)
     * @throws BusinessException if the associate is unable to vote (USER_UNABLE_TO_VOTE)
     * @throws BusinessException if the session doesn't exist (SESSION_NOT_FOUND)
     * @throws BusinessException if the session is closed (SESSION_CLOSED)
     * @throws BusinessException if the associate has already voted (DUPLICATE_VOTE)
     */
    @PostMapping("/{sessionId}/votes")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(
            summary = "Register a vote",
            description = """
                Registers a vote from an associate in an active voting session.

                **Vote Registration Process:**
                1. **CPF Validation**: Validates associate's CPF through external service
                2. **Session Validation**: Ensures session exists and is currently open
                3. **Duplicate Check**: Prevents multiple votes from same associate
                4. **Vote Recording**: Saves vote to database with transaction safety

                **Business Rules:**
                - CPF must be exactly 11 digits
                - Associate must be eligible to vote (CPF validation)
                - Session must be open (current time between opensAt and closesAt)
                - One vote per associate per session (enforced by unique constraint)
                - Vote choice must be YES or NO
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Vote accepted for processing"),
            @ApiResponse(responseCode = "400", description = "Validation error in request body"),
            @ApiResponse(responseCode = "404", description = "CPF validation failed"),
            @ApiResponse(responseCode = "422", description = "Business rule violation")
    })
    public void registerVote(
            @PathVariable Long sessionId,
            @RequestBody @Valid VoteRequestDTO voteRequest) {
        log.info("Processing vote registration for session ID: {} from CPF: {}", 
                sessionId, voteRequest.associateCpf());
        
        voteService.registerVote(sessionId, voteRequest);
        
        log.info("Vote registered successfully for session ID: {} (choice: {})", 
                sessionId, voteRequest.choice());
    }
}