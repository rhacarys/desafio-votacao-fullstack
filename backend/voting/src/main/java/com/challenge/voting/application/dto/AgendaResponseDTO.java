package com.challenge.voting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for agenda list responses.
 * Contains basic agenda information returned when listing or creating agendas.
 * This is a lightweight DTO used for aggregate responses without voting details.
 * 
 * @param id The unique identifier of the agenda
 * @param title The agenda title (max 150 characters)
 * @param description The agenda description (max 500 characters)
 */
@Schema(description = "Agenda basic information response")
public record AgendaResponseDTO(
        @Schema(description = "Unique identifier of the agenda", example = "1")
        Long id,

        @Schema(description = "Title of the agenda", example = "Q2 Budget Allocation", maxLength = 150)
        String title,

        @Schema(description = "Description of the agenda", example = "Decision on budget allocation across departments for Q2 2024", maxLength = 500)
        String description) {
}