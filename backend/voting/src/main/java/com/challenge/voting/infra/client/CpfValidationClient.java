package com.challenge.voting.infra.client;

import com.challenge.voting.application.dto.CpfStatusDTO;
import com.challenge.voting.domain.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Random;

/**
 * Client for validating CPF eligibility through an external service.
 * This is a mock implementation that simulates external CPF validation service behavior.
 * In production, this would connect to an actual CPF validation API.
 * 
 * <p>Mock behavior:
 * <ul>
 *   <li>20% chance of returning invalid CPF (throws BusinessException)</li>
 *   <li>80% chance of returning valid CPF with random voting eligibility</li>
 *   <li>50% chance of ABLE_TO_VOTE status for valid CPFs</li>
 *   <li>50% chance of UNABLE_TO_VOTE status for valid CPFs</li>
 *   <li>Includes 100ms simulated network delay</li>
 * </ul>
 * 
 * @author Voting Challenge API
 * @version 1.0.0
 */
@Slf4j
@Component
public class CpfValidationClient {

    private final Random random = new Random();

    /**
     * Validates a CPF and returns its voting eligibility status.
     * Simulates an external service call with delay and random responses.
     * 
     * @param cpf the CPF to validate
     * @return CpfStatusDTO with voting eligibility status
     * @throws BusinessException if the CPF is invalid (CPF_INVALID) - simulated 20% of the time
     */
    public CpfStatusDTO validateCpf(String cpf) {
        log.debug("Validating CPF: {}", cpf);
        
        // Simulating external delay
        try { 
            Thread.sleep(100); 
        } catch (InterruptedException ignored) {}

        // Randomly return 404 (Not Found) for invalid CPFs - 20% chance
        if (random.nextInt(10) < 2) {
            log.warn("CPF validation failed: Invalid or not found CPF: {}", cpf);
            throw new BusinessException("CPF_INVALID", "The provided CPF is invalid or not found");
        }

        // Randomly return ABLE or UNABLE for valid CPFs
        String status = random.nextBoolean() ? "ABLE_TO_VOTE" : "UNABLE_TO_VOTE";
        log.debug("CPF validation successful. CPF: {}, Status: {}", cpf, status);
        
        return new CpfStatusDTO(status);
    }
}