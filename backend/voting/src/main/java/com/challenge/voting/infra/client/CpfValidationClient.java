package com.challenge.voting.infra.client;

import com.challenge.voting.application.dto.CpfStatusDTO;
import com.challenge.voting.domain.exception.BusinessException;
import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class CpfValidationClient {

    private final Random random = new Random();

    public CpfStatusDTO validateCpf(String cpf) {
        // Simulating external delay
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}

        // Randomly return 404 (Not Found) for invalid CPFs
        if (random.nextInt(10) < 2) { // 20% chance of being "invalid"
            throw new BusinessException("CPF_INVALID", "The provided CPF is invalid or not found");
        }

        // Randomly return ABLE or UNABLE for valid CPFs
        String status = random.nextBoolean() ? "ABLE_TO_VOTE" : "UNABLE_TO_VOTE";
        return new CpfStatusDTO(status);
    }
}