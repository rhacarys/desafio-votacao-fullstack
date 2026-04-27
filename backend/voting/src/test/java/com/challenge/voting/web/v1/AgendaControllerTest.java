package com.challenge.voting.web.v1;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.challenge.voting.application.dto.AgendaDetailsDTO;
import com.challenge.voting.domain.Agenda;
import com.challenge.voting.domain.enums.AgendaStatus;
import com.challenge.voting.domain.exception.BusinessException;
import com.challenge.voting.repository.AgendaRepository;
import com.challenge.voting.service.AgendaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@WebMvcTest(AgendaController.class)
@AutoConfigureJson
class AgendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AgendaRepository agendaRepository;

    @MockitoBean
    private AgendaService agendaService;

    @Test
    void shouldCreateAgendaSuccessfully() throws Exception {
        AgendaController.CreateAgendaRequest request = new AgendaController.CreateAgendaRequest("Pauta 1",
                "Descrição 1");
        Agenda savedAgenda = Agenda.builder().id(1L).title("Pauta 1").description("Descrição 1").build();

        when(agendaRepository.save(any(Agenda.class))).thenReturn(savedAgenda);

        mockMvc.perform(post("/api/v1/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Pauta 1"))
                .andExpect(jsonPath("$.description").value("Descrição 1"));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingAgendaWithBlankTitle() throws Exception {
        AgendaController.CreateAgendaRequest request = new AgendaController.CreateAgendaRequest("", "Descrição 1");

        mockMvc.perform(post("/api/v1/agendas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void shouldListAllAgendas() throws Exception {
        Agenda agenda1 = Agenda.builder().id(1L).title("Pauta 1").description("Desc 1").build();
        Agenda agenda2 = Agenda.builder().id(2L).title("Pauta 2").description("Desc 2").build();

        when(agendaRepository.findAll()).thenReturn(List.of(agenda1, agenda2));

        mockMvc.perform(get("/api/v1/agendas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].title").value("Pauta 2"));
    }

    @Test
    void shouldGetAgendaDetailsSuccessfully() throws Exception {
        AgendaDetailsDTO detailsDTO = new AgendaDetailsDTO(
                1L, "Pauta 1", "Desc 1", AgendaStatus.OPEN, 100L, null, null, 5L, 2L, 7L);

        when(agendaService.getAgendaDetails(1L)).thenReturn(detailsDTO);

        mockMvc.perform(get("/api/v1/agendas/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.sessionId").value(100));
    }

    @Test
    void shouldReturnUnprocessableEntityWhenAgendaNotFound() throws Exception {
        when(agendaService.getAgendaDetails(99L))
                .thenThrow(new BusinessException("AGENDA_NOT_FOUND", "Agenda not found"));

        mockMvc.perform(get("/api/v1/agendas/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.code").value("AGENDA_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Agenda not found"));
    }
}