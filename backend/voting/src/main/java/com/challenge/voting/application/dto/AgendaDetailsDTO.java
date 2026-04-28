package com.challenge.voting.application.dto;

import com.challenge.voting.domain.enums.AgendaStatus;
import java.time.Instant;

public record AgendaDetailsDTO(
    Long id,
    String title,
    String description,
    AgendaStatus status,
    Long sessionId,
    Instant opensAt,
    Instant closesAt,
    Long yesVotes,
    Long noVotes,
    Long totalVotes
) {}