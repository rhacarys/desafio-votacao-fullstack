package com.challenge.voting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.challenge.voting.application.dto.VoteRequestDTO;
import com.challenge.voting.domain.Vote;
import com.challenge.voting.domain.VotingSession;
import com.challenge.voting.domain.exception.BusinessException;
import com.challenge.voting.repository.VoteRepository;
import com.challenge.voting.repository.VotingSessionRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VotingSessionRepository sessionRepository;

    @Transactional
    public void registerVote(Long sessionId, VoteRequestDTO request) {
        VotingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException("Voting session not found"));

        if (!session.isOpen()) {
            throw new BusinessException("Voting session is closed.");
        }

        if (voteRepository.existsByVotingSessionIdAndAssociateCpf(sessionId, request.associateCpf())) {
            throw new BusinessException("Associate has already voted in this session.");
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