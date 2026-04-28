package com.challenge.voting.application.dto;

import com.challenge.voting.domain.enums.VoteChoice;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object for vote registration requests.
 * Encapsulates the associate's CPF and their voting choice (YES or NO).
 * Validates that the CPF is provided and contains exactly 11 digits.
 * 
 * @param associateCpf The CPF of the voting associate (must be exactly 11 digits)
 * @param choice The voting choice (YES or NO)
 */
@Schema(description = "Vote registration request payload")
public record VoteRequestDTO(
        @Schema(description = "CPF of the voting associate (must be exactly 11 digits)", 
                example = "12345678901", pattern = "\\d{11}", minLength = 11, maxLength = 11)
        @NotBlank(message = "CPF is required")
        @Pattern(regexp = "\\d{11}", message = "CPF must contain exactly 11 digits")
        String associateCpf,

        @Schema(description = "The voting choice", example = "YES", allowableValues = {"YES", "NO"})
        @NotNull(message = "Vote choice is required")
        VoteChoice choice
) {}