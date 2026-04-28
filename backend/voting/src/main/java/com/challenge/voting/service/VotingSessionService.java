package com.challenge.voting.service;

import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.challenge.voting.domain.Agenda;
import com.challenge.voting.domain.VotingSession;
import com.challenge.voting.domain.exception.BusinessException;
import com.challenge.voting.repository.AgendaRepository;
import com.challenge.voting.repository.VotingSessionRepository;

/**
 * Service for managing voting sessions.
 * Handles the creation and validation of voting sessions for agendas.
 * Ensures that only one voting session can exist per agenda.
 * 
 * @author Voting Challenge API
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VotingSessionService {

    private final VotingSessionRepository sessionRepository;
    private final AgendaRepository agendaRepository;

    /**
     * Opens a new voting session for a specific agenda.
     * Validates that the agenda exists and no session already exists for it.
     * The session duration defaults to 1 minute if not specified or if invalid.
     * 
     * @param agendaId the ID of the agenda to open a voting session for
     * @param durationInMinutes the desired session duration (defaults to 1 if null or invalid)
     * @return the created VotingSession with calculated open and close times
     * @throws BusinessException if the agenda doesn't exist (AGENDA_NOT_FOUND)
     * @throws BusinessException if a session already exists (SESSION_ALREADY_EXISTS)
     */
    @Transactional
    public VotingSession openSession(Long agendaId, Integer durationInMinutes) {
        log.info("Opening voting session for agenda ID: {}", agendaId);
        
        Agenda agenda = agendaRepository.findById(agendaId)
                .orElseThrow(() -> {
                    log.warn("Failed to open session: Agenda not found with ID: {}", agendaId);
                    return new BusinessException("AGENDA_NOT_FOUND", "Agenda not found");
                });

        if (sessionRepository.findByAgendaId(agendaId).isPresent()) {
            log.warn("Failed to open session: A voting session already exists for agenda ID: {}", agendaId);
            throw new BusinessException("SESSION_ALREADY_EXISTS",
                    "A voting session is already open or has existed for this agenda.");
        }

        // Default 1 minute if not specified or invalid
        int duration = (durationInMinutes != null && durationInMinutes > 0) ? durationInMinutes : 1;
        Instant now = Instant.now();
        Instant closesAt = now.plus(Duration.ofMinutes(duration));

        VotingSession session = VotingSession.builder()
                .agenda(agenda)
                .opensAt(now)
                .closesAt(closesAt)
                .build();

        VotingSession saved = sessionRepository.save(session);
        log.info("Voting session created successfully. Session ID: {}, Duration: {} minutes, Opens at: {}, Closes at: {}", 
                saved.getId(), duration, saved.getOpensAt(), saved.getClosesAt());
        
        return saved;
    }
}