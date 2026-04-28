package com.challenge.voting.service;

import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.challenge.voting.domain.Agenda;
import com.challenge.voting.domain.VotingSession;
import com.challenge.voting.domain.exception.BusinessException;
import com.challenge.voting.repository.AgendaRepository;
import com.challenge.voting.repository.VotingSessionRepository;

@Service
@RequiredArgsConstructor
public class VotingSessionService {

    private final VotingSessionRepository sessionRepository;
    private final AgendaRepository agendaRepository;

    @Transactional
    public VotingSession openSession(Long agendaId, Integer durationInMinutes) {
        Agenda agenda = agendaRepository.findById(agendaId)
                .orElseThrow(() -> new BusinessException("AGENDA_NOT_FOUND", "Agenda not found"));

        if (sessionRepository.findByAgendaId(agendaId).isPresent()) {
            throw new BusinessException("SESSION_ALREADY_EXISTS",
                    "A voting session is already open or has existed for this agenda.");
        }

        // Default 1 minute if not specified or invalid
        int duration = (durationInMinutes != null && durationInMinutes > 0) ? durationInMinutes : 1;
        Instant now = Instant.now();

        VotingSession session = VotingSession.builder()
                .agenda(agenda)
                .opensAt(now)
                .closesAt(now.plus(Duration.ofMinutes(duration)))
                .build();

        return sessionRepository.save(session);
    }
}