package com.challenge.voting.repository;

import com.challenge.voting.domain.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Agenda persistence operations.
 * Provides data access methods for querying agendas stored in the database.
 * Extends JpaRepository to inherit standard CRUD and query operations.
 * 
 * @author Voting Challenge API
 * @version 1.0.0
 */
@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Long> {
}
