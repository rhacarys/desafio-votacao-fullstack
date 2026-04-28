package com.challenge.voting.application.dto;

import com.challenge.voting.domain.enums.AgendaStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * Data Transfer Object containing comprehensive agenda details including voting session information.
 * Used when retrieving detailed information about a specific agenda including voting results.
 * This DTO is cached after a voting session is closed to improve performance.
 * Note: Vote counts are only populated when the agenda status is CLOSED.
 * 
 * @param id The unique identifier of the agenda
 * @param title The agenda title
 * @param description The agenda description
 * @param status The current agenda status (PENDING, OPEN, or CLOSED)
 * @param sessionId The ID of the voting session (null if no session exists)
 * @param opensAt The timestamp when the voting session opened (null if no session)
 * @param closesAt The timestamp when the voting session will/did close (null if no session)
 * @param yesVotes Count of YES votes (0 if session is not closed)
 * @param noVotes Count of NO votes (0 if session is not closed)
 * @param totalVotes Total vote count (0 if session is not closed)
 */
@Schema(description = "Detailed agenda information with voting session and results")
public record AgendaDetailsDTO(
        @Schema(description = "Unique identifier of the agenda", example = "1")
        Long id,

        @Schema(description = "Title of the agenda", example = "Q2 Budget Allocation")
        String title,

        @Schema(description = "Description of the agenda", example = "Decision on budget allocation across departments for Q2 2024")
        String description,

        @Schema(description = "Current agenda status", example = "CLOSED", allowableValues = {"PENDING", "OPEN", "CLOSED"})
        AgendaStatus status,

        @Schema(description = "ID of the voting session (null if no session exists)", example = "1")
        Long sessionId,

        @Schema(description = "Timestamp when the voting session opened (null if no session)", example = "2024-04-28T10:00:00Z")
        Instant opensAt,

        @Schema(description = "Timestamp when the voting session will/did close (null if no session)", example = "2024-04-28T10:05:00Z")
        Instant closesAt,

        @Schema(description = "Count of YES votes (0 if session is not closed)", example = "15", minimum = "0")
        Long yesVotes,

        @Schema(description = "Count of NO votes (0 if session is not closed)", example = "8", minimum = "0")
        Long noVotes,

        @Schema(description = "Total vote count (0 if session is not closed)", example = "23", minimum = "0")
        Long totalVotes
) {}