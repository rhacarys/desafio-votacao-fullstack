package com.challenge.voting.domain.enums;

/**
 * Enumeration of possible voting choices in a voting session.
 * Associates can vote either YES (approval) or NO (rejection) on an agenda.
 * 
 * <p>Values:
 * <ul>
 *   <li>YES: Vote in favor of the agenda</li>
 *   <li>NO: Vote against the agenda</li>
 * </ul>
 * 
 * @author Voting Challenge API
 * @version 1.0.0
 */
public enum VoteChoice {
    YES,
    NO
}
