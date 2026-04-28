package com.challenge.voting.application.dto;

public record AgendaResultDTO(
        Long agendaId,
        String title,
        long totalVotes,
        long yesVotes,
        long noVotes,
        String result) {
}