package com.challenge.voting.application.dto;

import com.challenge.voting.domain.enums.AgendaStatus;
import java.time.LocalDateTime;

public record AgendaDetailsDTO(
    Long id,
    String title,
    String description,
    AgendaStatus status,
    Long sessionId,
    LocalDateTime opensAt,
    LocalDateTime closesAt,
    Long yesVotes,
    Long noVotes,
    Long totalVotes
) {}