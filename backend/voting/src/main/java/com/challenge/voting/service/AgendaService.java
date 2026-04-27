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
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final VotingSessionRepository sessionRepository;
    private final VoteRepository voteRepository;

    public AgendaDetailsDTO getAgendaDetails(Long agendaId) {
        Agenda agenda = agendaRepository.findById(agendaId)
                .orElseThrow(() -> new BusinessException("Agenda not found"));

        Optional<VotingSession> sessionOpt = sessionRepository.findByAgendaId(agendaId);

        AgendaStatus status = AgendaStatus.PENDING;
        Long sessionId = null;
        LocalDateTime opensAt = null;
        LocalDateTime closesAt = null;
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
            } else if (LocalDateTime.now().isAfter(closesAt)) {
                status = AgendaStatus.CLOSED;

                yesVotes = voteRepository.countByVotingSessionIdAndChoice(session.getId(), VoteChoice.YES);
                noVotes = voteRepository.countByVotingSessionIdAndChoice(session.getId(), VoteChoice.NO);
                totalVotes = yesVotes + noVotes;
            }
        }

        return new AgendaDetailsDTO(
        agenda.getId(), agenda.getTitle(), agenda.getDescription(),
        status, sessionId, opensAt, closesAt, yesVotes, noVotes, totalVotes
);
    }
}