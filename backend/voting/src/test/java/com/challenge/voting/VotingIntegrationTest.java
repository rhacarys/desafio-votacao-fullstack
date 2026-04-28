package com.challenge.voting;

import com.challenge.voting.application.dto.CpfStatusDTO;
import com.challenge.voting.application.dto.VoteRequestDTO;
import com.challenge.voting.domain.Agenda;
import com.challenge.voting.domain.VotingSession;
import com.challenge.voting.domain.enums.VoteChoice;
import com.challenge.voting.infra.client.CpfValidationClient;
import com.challenge.voting.repository.AgendaRepository;
import com.challenge.voting.repository.VotingSessionRepository;
import com.challenge.voting.web.v1.AgendaController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class VotingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AgendaRepository agendaRepository;

    @Autowired
    private VotingSessionRepository sessionRepository;

    @MockitoBean
    private CpfValidationClient cpfClient;

    @BeforeEach
    void setUp() {
        when(cpfClient.validateCpf(anyString())).thenReturn(new CpfStatusDTO("ABLE_TO_VOTE"));
    }

    @Test
    @DisplayName("Full successful flow (Create -> Open -> Vote)")
    void shouldExecuteFullVotingFlow() throws Exception {
        var agendaRequest = new AgendaController.CreateAgendaRequest("New Project", "Integration testing");
        
        var agendaResult = mockMvc.perform(post("/api/v1/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(agendaRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        Long agendaId = Long.valueOf(objectMapper.readTree(agendaResult.getResponse().getContentAsString()).get("id").asText());

        var sessionResult = mockMvc.perform(post("/api/v1/sessions/open")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"agendaId\": %d, \"durationInMinutes\": 10}".formatted(agendaId)))
                .andExpect(status().isCreated())
                .andReturn();

        Long sessionId = Long.valueOf(objectMapper.readTree(sessionResult.getResponse().getContentAsString()).get("sessionId").asText());

        var voteRequest = new VoteRequestDTO("12345678901", VoteChoice.YES);
        mockMvc.perform(post("/api/v1/sessions/" + sessionId + "/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("Prevent duplicate voting from same associate")
    void shouldPreventDuplicateVote() throws Exception {
        Agenda agenda = agendaRepository.save(Agenda.builder().title("Unique").description("Desc").build());
        VotingSession session = sessionRepository.save(VotingSession.builder()
                .agenda(agenda)
                .opensAt(Instant.now())
                .closesAt(Instant.now().plus(1, ChronoUnit.MINUTES))
                .build());

        var vote = new VoteRequestDTO("11122233344", VoteChoice.NO);

        mockMvc.perform(post("/api/v1/sessions/" + session.getId() + "/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vote)))
                .andExpect(status().isAccepted());

        // Second attempt
        mockMvc.perform(post("/api/v1/sessions/" + session.getId() + "/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vote)))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.code").value("DUPLICATE_VOTE"));
    }

    @Test
    @DisplayName("Reject voting on expired sessions")
    void shouldRejectVoteOnClosedSession() throws Exception {
        Agenda agenda = agendaRepository.save(Agenda.builder().title("Expired").description("Desc").build());
        VotingSession session = sessionRepository.save(VotingSession.builder()
                .agenda(agenda)
                .opensAt(Instant.now().minus(10, ChronoUnit.MINUTES))
                .closesAt(Instant.now().minus(1, ChronoUnit.MINUTES)) // Already closed
                .build());

        var vote = new VoteRequestDTO("55566677788", VoteChoice.YES);

        mockMvc.perform(post("/api/v1/sessions/" + session.getId() + "/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vote)))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.code").value("SESSION_CLOSED"));
    }

    @Test
    @DisplayName("Validation constraints for agenda creation")
    void shouldReturnBadRequestForInvalidAgenda() throws Exception {
        var invalidRequest = new AgendaController.CreateAgendaRequest("", ""); // Blank fields

        mockMvc.perform(post("/api/v1/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("Verify agenda list persistence")
    void shouldPersistAndListAgendas() throws Exception {
        agendaRepository.save(Agenda.builder().title("Agenda A").description("D1").build());
        agendaRepository.save(Agenda.builder().title("Agenda B").description("D2").build());

        mockMvc.perform(get("/api/v1/agendas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Agenda A"))
                .andExpect(jsonPath("$[1].title").value("Agenda B"));
    }
}