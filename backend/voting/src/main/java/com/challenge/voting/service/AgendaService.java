package com.challenge.voting.service;

import com.challenge.voting.application.dto.AgendaDetailsDTO;
import com.challenge.voting.domain.Agenda;
import com.challenge.voting.domain.VotingSession;
import com.challenge.voting.domain.enums.AgendaStatus;
import com.challenge.voting.domain.enums.VoteChoice;
import com.challenge.voting.domain.exception.BusinessException;
import com.challenge.voting.repository.AgendaRepository;
import com.challenge.voting.repository.VoteRepository;
import com.challenge.voting.repository.VotingSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;

/**
 * Service for agenda-related business logic.
 * Handles retrieval of agenda details including voting session information and results.
 * Results are cached after a voting session is closed to improve performance.
 * 
 * @author Voting Challenge API
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final VotingSessionRepository sessionRepository;
    private final VoteRepository voteRepository;

    /**
     * Retrieves comprehensive details about an agenda including voting session information.
     * Calculates the current status based on voting session state:
     * - PENDING: No voting session exists
     * - OPEN: Voting session is currently active
     * - CLOSED: Voting session has ended
     * 
     * Results are cached once a session is closed to avoid repeated database queries.
     * 
     * @param agendaId the ID of the agenda to retrieve
     * @return AgendaDetailsDTO with complete agenda information including vote counts
     * @throws BusinessException if the agenda is not found (AGENDA_NOT_FOUND)
     */
    @Cacheable(value = "agendaResults", key = "#agendaId", unless = "#result.status != 'CLOSED'")
    public AgendaDetailsDTO getAgendaDetails(Long agendaId) {
        log.debug("Retrieving details for agenda ID: {}", agendaId);
        
        Agenda agenda = agendaRepository.findById(agendaId)
                .orElseThrow(() -> {
                    log.warn("Agenda not found with ID: {}", agendaId);
                    return new BusinessException("AGENDA_NOT_FOUND", "Agenda not found");
                });

        Optional<VotingSession> sessionOpt = sessionRepository.findByAgendaId(agendaId);

        AgendaStatus status = AgendaStatus.PENDING;
        Long sessionId = null;
        Instant opensAt = null;
        Instant closesAt = null;
        Long yesVotes = 0L;
        Long noVotes = 0L;
        Long totalVotes = 0L;

        if (sessionOpt.isPresent()) {
            VotingSession session = sessionOpt.get();
            sessionId = session.getId();
            opensAt = session.getOpensAt();
            closesAt = session.getClosesAt();

            if (session.isOpen()) {
                status = AgendaStatus.OPEN;
                log.debug("Agenda ID: {} is in OPEN status", agendaId);
            } else if (Instant.now().isAfter(closesAt)) {
                status = AgendaStatus.CLOSED;
                
                yesVotes = voteRepository.countByVotingSessionIdAndChoice(session.getId(), VoteChoice.YES);
                noVotes = voteRepository.countByVotingSessionIdAndChoice(session.getId(), VoteChoice.NO);
                totalVotes = yesVotes + noVotes;
                
                log.info("Agenda ID: {} voting session closed. Results - YES: {}, NO: {}, TOTAL: {}", 
                        agendaId, yesVotes, noVotes, totalVotes);
            }
        }

        return new AgendaDetailsDTO(
                agenda.getId(), agenda.getTitle(), agenda.getDescription(),
                status, sessionId, opensAt, closesAt, yesVotes, noVotes, totalVotes);
    }
}