package com.challenge.voting.repository;

import com.challenge.voting.domain.Vote;
import com.challenge.voting.domain.enums.VoteChoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Vote persistence operations.
 * Provides data access methods for querying votes stored in the database.
 * Extends JpaRepository to inherit standard CRUD operations.
 * 
 * <p>Custom query methods:
 * <ul>
 *   <li>{@link #existsByVotingSessionIdAndAssociateCpf(Long, String)}: Check for duplicate votes</li>
 *   <li>{@link #countByVotingSessionIdAndChoice(Long, VoteChoice)}: Count votes by choice</li>
 * </ul>
 * 
 * @author Voting Challenge API
 * @version 1.0.0
 */
@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    
    /**
     * Checks if an associate has already voted in a specific voting session.
     * Used to enforce the rule that each associate can vote only once per session.
     * 
     * @param votingSessionId the ID of the voting session
     * @param associateCpf the CPF of the associate
     * @return true if a vote exists, false otherwise
     */
    boolean existsByVotingSessionIdAndAssociateCpf(Long votingSessionId, String associateCpf);

    /**
     * Counts the number of votes for a specific choice in a voting session.
     * Used to calculate voting results after a session closes.
     * 
     * @param votingSessionId the ID of the voting session
     * @param choice the vote choice (YES or NO)
     * @return the count of votes for the specified choice
     */
    long countByVotingSessionIdAndChoice(Long votingSessionId, VoteChoice choice);
}
