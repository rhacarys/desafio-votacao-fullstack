package com.challenge.voting.web.v1;

import com.challenge.voting.application.dto.AgendaDetailsDTO;
import com.challenge.voting.application.dto.AgendaResponseDTO;
import com.challenge.voting.domain.Agenda;
import com.challenge.voting.repository.AgendaRepository;
import com.challenge.voting.service.AgendaService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/agendas")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaRepository agendaRepository;
    private final AgendaService agendaService;

    public record CreateAgendaRequest(
            @NotBlank(message = "Title is required") String title,
            @NotBlank(message = "Description is required") String description) {
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgendaResponseDTO create(@RequestBody @Valid CreateAgendaRequest request) {
        Agenda agenda = Agenda.builder()
                .title(request.title())
                .description(request.description())
                .build();

        Agenda saved = agendaRepository.save(agenda);

        return new AgendaResponseDTO(saved.getId(), saved.getTitle(), saved.getDescription());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AgendaResponseDTO> listAll() {
        return agendaRepository.findAll().stream()
                .map(a -> new AgendaResponseDTO(a.getId(), a.getTitle(), a.getDescription()))
                .toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AgendaDetailsDTO getDetails(@PathVariable Long id) {
        return agendaService.getAgendaDetails(id);
    }
}