package com.challenge.voting.domain.enums;

/**
 * Enumeration representing the lifecycle status of an agenda in the voting system.
 * Tracks the progression of an agenda from creation through voting completion.
 * 
 * <p>Status progression:
 * <ul>
 *   <li>PENDING: Initial state - agenda exists but no voting session has been opened yet</li>
 *   <li>OPEN: A voting session is currently active, associates can vote</li>
 *   <li>CLOSED: The voting session has ended, results are finalized and cached</li>
 * </ul>
 * 
 * @author Voting Challenge API
 * @version 1.0.0
 */
public enum AgendaStatus {
    PENDING,
    OPEN,
    CLOSED
}