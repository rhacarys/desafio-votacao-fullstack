package com.challenge.voting.domain.exception;

import lombok.Getter;

/**
 * Custom exception for business rule violations in the voting system.
 * Extends RuntimeException to provide unchecked exception handling.
 * Each BusinessException includes an error code for programmatic error handling.
 * 
 * <p>Common error codes:
 * <ul>
 *   <li>AGENDA_NOT_FOUND: Requested agenda does not exist</li>
 *   <li>SESSION_NOT_FOUND: Requested voting session does not exist</li>
 *   <li>SESSION_ALREADY_EXISTS: A voting session already exists for this agenda</li>
 *   <li>SESSION_CLOSED: The voting session is no longer open</li>
 *   <li>DUPLICATE_VOTE: Associate has already voted in this session</li>
 *   <li>USER_UNABLE_TO_VOTE: Associate is not eligible to vote</li>
 *   <li>CPF_INVALID: Provided CPF is invalid or not found</li>
 * </ul>
 * 
 * @author Voting Challenge API
 * @version 1.0.0
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String code;

    /**
     * Constructs a BusinessException with an error code and message.
     * 
     * @param code A machine-readable error code for error handling
     * @param message A human-readable error message
     */
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
}