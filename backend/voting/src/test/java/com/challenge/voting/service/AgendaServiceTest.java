package com.challenge.voting.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.challenge.voting.application.dto.AgendaDetailsDTO;
import com.challenge.voting.domain.Agenda;
import com.challenge.voting.domain.VotingSession;
import com.challenge.voting.domain.enums.AgendaStatus;
import com.challenge.voting.domain.enums.VoteChoice;
import com.challenge.voting.domain.exception.BusinessException;
import com.challenge.voting.repository.AgendaRepository;
import com.challenge.voting.repository.VoteRepository;
import com.challenge.voting.repository.VotingSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AgendaServiceTest {

    @Mock
    private AgendaRepository agendaRepository;

    @Mock
    private VotingSessionRepository sessionRepository;

    @Mock
    private VoteRepository voteRepository;

    @InjectMocks
    private AgendaService agendaService;

    private Agenda agendaMock;
    private VotingSession sessionMock;

    @BeforeEach
    void setUp() {
        agendaMock = mock(Agenda.class);
        sessionMock = mock(VotingSession.class);

        lenient().when(agendaMock.getId()).thenReturn(1L);
        lenient().when(agendaMock.getTitle()).thenReturn("Test Agenda");
        lenient().when(agendaMock.getDescription()).thenReturn("Description");
        
        lenient().when(sessionMock.getId()).thenReturn(100L);
        lenient().when(sessionMock.getOpensAt()).thenReturn(Instant.now().minus(5, ChronoUnit.MINUTES));
    }

    @Test
    void shouldThrowExceptionWhenAgendaNotFound() {
        Long agendaId = 1L;
        when(agendaRepository.findById(agendaId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> agendaService.getAgendaDetails(agendaId));

        assertEquals("AGENDA_NOT_FOUND", exception.getCode());
        verify(sessionRepository, never()).findByAgendaId(anyLong());
    }

    @Test
    void shouldReturnPendingStatusWhenNoSessionExists() {
        Long agendaId = 1L;
        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agendaMock));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.empty());

        AgendaDetailsDTO result = agendaService.getAgendaDetails(agendaId);

        assertEquals(AgendaStatus.PENDING, result.status());
        assertNull(result.sessionId());
        assertEquals(0L, result.totalVotes());
    }

    @Test
    void shouldReturnOpenStatusWhenSessionIsOpen() {
        Long agendaId = 1L;
        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agendaMock));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(sessionMock));
        when(sessionMock.isOpen()).thenReturn(true);
        when(sessionMock.getClosesAt()).thenReturn(Instant.now().plus(5, ChronoUnit.MINUTES));

        AgendaDetailsDTO result = agendaService.getAgendaDetails(agendaId);

        assertEquals(AgendaStatus.OPEN, result.status());
        assertEquals(100L, result.sessionId());
        assertNotNull(result.closesAt());
        assertEquals(0L, result.yesVotes());
        assertEquals(0L, result.totalVotes());
        verify(voteRepository, never()).countByVotingSessionIdAndChoice(anyLong(), any());
    }

    @Test
    void shouldReturnClosedStatusAndCalculateVotesWhenSessionIsClosed() {
        Long agendaId = 1L;
        Long sessionId = 100L;
        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agendaMock));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(sessionMock));
        when(sessionMock.isOpen()).thenReturn(false);
        when(sessionMock.getClosesAt()).thenReturn(Instant.now().minus(1, ChronoUnit.MINUTES)); // Already closed

        when(voteRepository.countByVotingSessionIdAndChoice(sessionId, VoteChoice.YES)).thenReturn(10L);
        when(voteRepository.countByVotingSessionIdAndChoice(sessionId, VoteChoice.NO)).thenReturn(5L);

        AgendaDetailsDTO result = agendaService.getAgendaDetails(agendaId);

        assertEquals(AgendaStatus.CLOSED, result.status());
        assertEquals(sessionId, result.sessionId());
        assertEquals(10L, result.yesVotes());
        assertEquals(5L, result.noVotes());
        assertEquals(15L, result.totalVotes());
    }
}