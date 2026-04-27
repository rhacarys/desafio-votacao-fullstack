package com.challenge.voting.web.v1;

import com.challenge.voting.application.dto.VoteRequestDTO;
import com.challenge.voting.domain.Agenda;
import com.challenge.voting.domain.VotingSession;
import com.challenge.voting.domain.enums.VoteChoice;
import com.challenge.voting.domain.exception.BusinessException;
import com.challenge.voting.service.VoteService;
import com.challenge.voting.service.VotingSessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VotingSessionController.class)
class VotingSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private VotingSessionService sessionService;

    @MockitoBean
    private VoteService voteService;

    private VotingSession testSession;
    private Agenda testAgenda;

    @BeforeEach
    void setUp() {
        testAgenda = Agenda.builder()
                .id(1L)
                .title("Test Agenda")
                .description("Test Description")
                .build();

        testSession = VotingSession.builder()
                .id(100L)
                .agenda(testAgenda)
                .opensAt(LocalDateTime.now().minusMinutes(5))
                .closesAt(LocalDateTime.now().plusMinutes(5))
                .build();
    }

    @Test
    void openSession_ShouldReturnCreatedSession() throws Exception {
        VotingSessionController.OpenSessionRequest request = new VotingSessionController.OpenSessionRequest(1L, 10);

        when(sessionService.openSession(1L, 10)).thenReturn(testSession);

        mockMvc.perform(post("/api/v1/sessions/open")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sessionId").value(100L))
                .andExpect(jsonPath("$.agendaId").value(1L));
    }

    @Test
    void openSession_ShouldReturnUnprocessableEntity_WhenAgendaNotFound() throws Exception {
        VotingSessionController.OpenSessionRequest request = new VotingSessionController.OpenSessionRequest(999L, 10);

        when(sessionService.openSession(999L, 10))
                .thenThrow(new BusinessException("AGENDA_NOT_FOUND", "Agenda not found"));

        mockMvc.perform(post("/api/v1/sessions/open")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("AGENDA_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Agenda not found"));
    }

    @Test
    void openSession_ShouldReturnUnprocessableEntity_WhenSessionAlreadyExists() throws Exception {
        VotingSessionController.OpenSessionRequest request = new VotingSessionController.OpenSessionRequest(1L, 10);

        when(sessionService.openSession(1L, 10))
                .thenThrow(new BusinessException("SESSION_ALREADY_EXISTS", "A voting session is already open or has existed for this agenda."));

        mockMvc.perform(post("/api/v1/sessions/open")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("SESSION_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message").value("A voting session is already open or has existed for this agenda."));
    }

    @Test
    void registerVote_ShouldReturnAccepted() throws Exception {
        VoteRequestDTO request = new VoteRequestDTO("12345678901", VoteChoice.YES);

        mockMvc.perform(post("/api/v1/sessions/100/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());
    }

    @Test
    void registerVote_ShouldReturnBadRequest_WhenCpfIsInvalid() throws Exception {
        VoteRequestDTO request = new VoteRequestDTO("", VoteChoice.YES);

        mockMvc.perform(post("/api/v1/sessions/100/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerVote_ShouldReturnUnprocessableEntity_WhenSessionNotFound() throws Exception {
        VoteRequestDTO request = new VoteRequestDTO("12345678901", VoteChoice.YES);

        doThrow(new BusinessException("SESSION_NOT_FOUND", "Voting session not found"))
                .when(voteService).registerVote(eq(999L), any(VoteRequestDTO.class));

        mockMvc.perform(post("/api/v1/sessions/999/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("SESSION_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Voting session not found"));
    }

    @Test
    void registerVote_ShouldReturnUnprocessableEntity_WhenSessionClosed() throws Exception {
        VoteRequestDTO request = new VoteRequestDTO("12345678901", VoteChoice.YES);

        doThrow(new BusinessException("SESSION_CLOSED", "Voting session is closed."))
                .when(voteService).registerVote(eq(100L), any(VoteRequestDTO.class));

        mockMvc.perform(post("/api/v1/sessions/100/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("SESSION_CLOSED"))
                .andExpect(jsonPath("$.message").value("Voting session is closed."));
    }

    @Test
    void registerVote_ShouldReturnUnprocessableEntity_WhenDuplicateVote() throws Exception {
        VoteRequestDTO request = new VoteRequestDTO("12345678901", VoteChoice.YES);

        doThrow(new BusinessException("DUPLICATE_VOTE", "Associate has already voted in this session."))
                .when(voteService).registerVote(eq(100L), any(VoteRequestDTO.class));

        mockMvc.perform(post("/api/v1/sessions/100/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("DUPLICATE_VOTE"))
                .andExpect(jsonPath("$.message").value("Associate has already voted in this session."));
    }
}