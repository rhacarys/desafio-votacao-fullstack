package com.challenge.voting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.challenge.voting.application.dto.CpfStatusDTO;
import com.challenge.voting.application.dto.VoteRequestDTO;
import com.challenge.voting.domain.Vote;
import com.challenge.voting.domain.VotingSession;
import com.challenge.voting.domain.exception.BusinessException;
import com.challenge.voting.infra.client.CpfValidationClient;
import com.challenge.voting.repository.VoteRepository;
import com.challenge.voting.repository.VotingSessionRepository;

/**
 * Service for managing vote registration.
 * Handles validation and recording of votes in voting sessions.
 * Enforces business rules including CPF validation, session status checks, and duplicate prevention.
 * 
 * <p>Vote registration workflow:
 * <ol>
 *   <li>Validate CPF using external CPF validation service</li>
 *   <li>Retrieve the voting session and verify it's still open</li>
 *   <li>Check that the associate hasn't already voted in this session</li>
 *   <li>Record the vote in the database</li>
 * </ol>
 * 
 * @author Voting Challenge API
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VoteService {

    private final CpfValidationClient cpfClient;
    private final VoteRepository voteRepository;
    private final VotingSessionRepository sessionRepository;

    /**
     * Registers a vote from an associate in a voting session.
     * Performs comprehensive validation before recording the vote:
     * 1. Validates CPF eligibility
     * 2. Verifies voting session exists and is open
     * 3. Prevents duplicate votes from the same associate
     * 
     * @param sessionId the ID of the voting session
     * @param request the vote request containing CPF and choice
     * @throws BusinessException if CPF is invalid (CPF_INVALID)
     * @throws BusinessException if associate cannot vote (USER_UNABLE_TO_VOTE)
     * @throws BusinessException if session doesn't exist (SESSION_NOT_FOUND)
     * @throws BusinessException if voting session is closed (SESSION_CLOSED)
     * @throws BusinessException if duplicate vote detected (DUPLICATE_VOTE)
     */
    @Transactional
    public void registerVote(Long sessionId, VoteRequestDTO request) {
        log.info("Starting validation for CPF: {}", request.associateCpf());

        CpfStatusDTO cpfStatus = cpfClient.validateCpf(request.associateCpf());
        if ("UNABLE_TO_VOTE".equals(cpfStatus.status())) {
            log.warn("Associate {} is unable to vote", request.associateCpf());
            throw new BusinessException("USER_UNABLE_TO_VOTE", "This associate is not allowed to vote at this time");
        }

        VotingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> {
                    log.warn("Voting session not found with ID: {}", sessionId);
                    return new BusinessException("SESSION_NOT_FOUND", "Voting session not found");
                });

        if (!session.isOpen()) {
            log.warn("Attempted vote in closed session. Session ID: {}", sessionId);
            throw new BusinessException("SESSION_CLOSED", "Voting session is closed.");
        }

        if (voteRepository.existsByVotingSessionIdAndAssociateCpf(sessionId, request.associateCpf())) {
            log.warn("Duplicate vote attempt. Session ID: {}, CPF: {}", sessionId, request.associateCpf());
            throw new BusinessException("DUPLICATE_VOTE", "Associate has already voted in this session.");
        }

        Vote vote = Vote.builder()
                .votingSession(session)
                .associateCpf(request.associateCpf())
                .choice(request.choice())
                .build();

        voteRepository.save(vote);
        log.info("Vote registered successfully for session {} by CPF: {}", sessionId, request.associateCpf());
    }
}