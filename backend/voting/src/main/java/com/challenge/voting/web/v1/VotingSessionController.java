package com.challenge.voting.web.v1;

import com.challenge.voting.application.dto.VoteRequestDTO;
import com.challenge.voting.domain.VotingSession;
import com.challenge.voting.service.VoteService;
import com.challenge.voting.service.VotingSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class VotingSessionController {

    private final VotingSessionService sessionService;
    private final VoteService voteService;

    public record OpenSessionRequest(
            Long agendaId,
            Integer durationInMinutes) {
    }

    public record SessionResponse(
            Long sessionId,
            Long agendaId,
            LocalDateTime opensAt,
            LocalDateTime closesAt) {
    }

    @PostMapping("/open")
    @ResponseStatus(HttpStatus.CREATED)
    public SessionResponse openSession(@RequestBody OpenSessionRequest request) {
        VotingSession session = sessionService.openSession(request.agendaId(), request.durationInMinutes());
        return new SessionResponse(
                session.getId(),
                session.getAgenda().getId(),
                session.getOpensAt(),
                session.getClosesAt());
    }

    @PostMapping("/{sessionId}/votes")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void registerVote(
            @PathVariable Long sessionId,
            @RequestBody @Valid VoteRequestDTO voteRequest) {
        voteService.registerVote(sessionId, voteRequest);
    }
}