package com.challenge.voting.repository;

import com.challenge.voting.domain.VotingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VotingSessionRepository extends JpaRepository<VotingSession, Long> {
    Optional<VotingSession> findByAgendaId(Long agendaId);
}
