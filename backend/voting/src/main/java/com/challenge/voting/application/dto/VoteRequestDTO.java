package com.challenge.voting.application.dto;

import com.challenge.voting.domain.enums.VoteChoice;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record VoteRequestDTO(
        @NotBlank(message = "CPF is required")
        @Pattern(regexp = "\\d{11}", message = "CPF must contain exactly 11 digits")
        String associateCpf,

        @NotNull(message = "Vote choice is required")
        VoteChoice choice
) {}