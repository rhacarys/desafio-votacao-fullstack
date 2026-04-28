package com.challenge.voting.repository;

import com.challenge.voting.domain.VotingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository for VotingSession persistence operations.
 * Provides data access methods for querying voting sessions stored in the database.
 * Extends JpaRepository to inherit standard CRUD operations.
 * 
 * <p>Custom query methods:
 * <ul>
 *   <li>{@link #findByAgendaId(Long)}: Find voting session by agenda ID (one-to-one relationship)</li>
 * </ul>
 * 
 * @author Voting Challenge API
 * @version 1.0.0
 */
@Repository
public interface VotingSessionRepository extends JpaRepository<VotingSession, Long> {
    
    /**
     * Retrieves a voting session associated with a specific agenda.
     * Since each agenda has at most one voting session, this returns an Optional.
     * 
     * @param agendaId the ID of the agenda
     * @return an Optional containing the voting session if it exists, or empty otherwise
     */
    Optional<VotingSession> findByAgendaId(Long agendaId);
}
