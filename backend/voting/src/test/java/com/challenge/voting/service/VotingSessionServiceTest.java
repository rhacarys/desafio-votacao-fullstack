package com.challenge.voting.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.challenge.voting.domain.Agenda;
import com.challenge.voting.domain.VotingSession;
import com.challenge.voting.domain.exception.BusinessException;
import com.challenge.voting.repository.AgendaRepository;
import com.challenge.voting.repository.VotingSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class VotingSessionServiceTest {

    @Mock
    private VotingSessionRepository sessionRepository;

    @Mock
    private AgendaRepository agendaRepository;

    @InjectMocks
    private VotingSessionService sessionService;

    @Captor
    private ArgumentCaptor<VotingSession> sessionCaptor;

    private Agenda agendaMock;

    @BeforeEach
    void setUp() {
        agendaMock = mock(Agenda.class);
        lenient().when(agendaMock.getId()).thenReturn(1L);
    }

    @Test
    void shouldOpenSessionWithSpecificDuration() {
        Long agendaId = 1L;
        Integer duration = 5;

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agendaMock));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.empty());
        when(sessionRepository.save(any(VotingSession.class))).thenAnswer(i -> i.getArguments()[0]);

        VotingSession result = sessionService.openSession(agendaId, duration);

        assertNotNull(result);
        verify(sessionRepository).save(sessionCaptor.capture());
        VotingSession savedSession = sessionCaptor.getValue();
        
        assertEquals(agendaMock, savedSession.getAgenda());
        assertNotNull(savedSession.getOpensAt());
        assertNotNull(savedSession.getClosesAt());
        
        LocalDateTime expectedClosesAt = savedSession.getOpensAt().plusMinutes(5);
        assertEquals(expectedClosesAt, savedSession.getClosesAt());
    }

    @Test
    void shouldOpenSessionWithDefaultDurationWhenNullOrInvalidProvided() {
        Long agendaId = 1L;

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agendaMock));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.empty());
        when(sessionRepository.save(any(VotingSession.class))).thenAnswer(i -> i.getArguments()[0]);

        VotingSession resultNull = sessionService.openSession(agendaId, null);
        assertEquals(resultNull.getOpensAt().plusMinutes(1), resultNull.getClosesAt());

        VotingSession resultZero = sessionService.openSession(agendaId, 0);
        assertEquals(resultZero.getOpensAt().plusMinutes(1), resultZero.getClosesAt());
    }

    @Test
    void shouldThrowExceptionWhenAgendaNotFound() {
        Long agendaId = 1L;

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, 
                () -> sessionService.openSession(agendaId, 5));

        assertEquals("AGENDA_NOT_FOUND", exception.getCode());
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenSessionAlreadyExists() {
        Long agendaId = 1L;
        VotingSession existingSession = mock(VotingSession.class);

        when(agendaRepository.findById(agendaId)).thenReturn(Optional.of(agendaMock));
        when(sessionRepository.findByAgendaId(agendaId)).thenReturn(Optional.of(existingSession));

        BusinessException exception = assertThrows(BusinessException.class, 
                () -> sessionService.openSession(agendaId, 5));

        assertEquals("SESSION_ALREADY_EXISTS", exception.getCode());
        verify(sessionRepository, never()).save(any());
    }
}