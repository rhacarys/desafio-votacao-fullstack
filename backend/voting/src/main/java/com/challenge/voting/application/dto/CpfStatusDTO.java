package com.challenge.voting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object representing the validation status of a CPF.
 * Returned by the external CPF validation service to indicate whether an associate is eligible to vote.
 * Status values: "ABLE_TO_VOTE" or "UNABLE_TO_VOTE"
 * 
 * @param status The CPF status indicating voting eligibility
 */
@Schema(description = "CPF validation status response")
public record CpfStatusDTO(
        @Schema(description = "CPF validation status", 
                example = "ABLE_TO_VOTE", 
                allowableValues = {"ABLE_TO_VOTE", "UNABLE_TO_VOTE"})
        String status) {
}