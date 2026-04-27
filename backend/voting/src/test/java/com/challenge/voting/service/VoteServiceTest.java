package com.challenge.voting.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.challenge.voting.application.dto.VoteRequestDTO;
import com.challenge.voting.domain.Vote;
import com.challenge.voting.domain.VotingSession;
import com.challenge.voting.domain.enums.VoteChoice;
import com.challenge.voting.domain.exception.BusinessException;
import com.challenge.voting.repository.VoteRepository;
import com.challenge.voting.repository.VotingSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private VotingSessionRepository sessionRepository;

    @InjectMocks
    private VoteService voteService;

    private VotingSession sessionMock;
    private VoteRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        sessionMock = mock(VotingSession.class);
        requestDTO = new VoteRequestDTO("12345678901", VoteChoice.YES);
    }

    @Test
    void shouldRegisterVoteSuccessfully() {
        Long sessionId = 1L;

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(sessionMock));
        when(sessionMock.isOpen()).thenReturn(true);
        when(voteRepository.existsByVotingSessionIdAndAssociateCpf(sessionId, requestDTO.associateCpf()))
                .thenReturn(false);

        assertDoesNotThrow(() -> voteService.registerVote(sessionId, requestDTO));
        verify(voteRepository, times(1)).save(any(Vote.class));
    }

    @Test
    void shouldThrowExceptionWhenSessionNotFound() {
        Long sessionId = 1L;

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> voteService.registerVote(sessionId, requestDTO));

        assertEquals("SESSION_NOT_FOUND", exception.getCode());
        verify(voteRepository, never()).save(any(Vote.class));
    }

    @Test
    void shouldThrowExceptionWhenSessionIsClosed() {
        Long sessionId = 1L;

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(sessionMock));
        when(sessionMock.isOpen()).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> voteService.registerVote(sessionId, requestDTO));

        assertEquals("SESSION_CLOSED", exception.getCode());
        verify(voteRepository, never()).save(any(Vote.class));
    }

    @Test
    void shouldThrowExceptionWhenDuplicateVote() {
        Long sessionId = 1L;

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(sessionMock));
        when(sessionMock.isOpen()).thenReturn(true);
        when(voteRepository.existsByVotingSessionIdAndAssociateCpf(sessionId, requestDTO.associateCpf()))
                .thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> voteService.registerVote(sessionId, requestDTO));

        assertEquals("DUPLICATE_VOTE", exception.getCode());
        verify(voteRepository, never()).save(any(Vote.class));
    }
}