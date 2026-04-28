package com.challenge.voting.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/**
 * Domain model representing a voting session for an agenda.
 * Each voting session has a defined time window during which associates can vote on an agenda.
 * A voting session is identified by its unique relationship to an agenda (one-to-one).
 * 
 * <p>Attributes:
 * <ul>
 *   <li>id: Unique identifier (auto-generated)</li>
 *   <li>agenda: Reference to the agenda being voted on (unique, required)</li>
 *   <li>opensAt: Timestamp when the session opens (required)</li>
 *   <li>closesAt: Timestamp when the session closes (required)</li>
 * </ul>
 * 
 * <p>The session enforces time-based voting eligibility through the {@link #isOpen()} method,
 * which validates that the current time falls between opensAt and closesAt.
 * 
 * @author Voting Challenge API
 * @version 1.0.0
 */
@Entity
@Table(name = "voting_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_id", nullable = false, unique = true)
    private Agenda agenda;

    @Column(name = "opens_at", nullable = false)
    private Instant opensAt;

    @Column(name = "closes_at", nullable = false)
    private Instant closesAt;

    /**
     * Determines if this voting session is currently open for voting.
     * A session is considered open if the current time is after opensAt and before closesAt.
     * 
     * @return true if the voting session is currently open, false otherwise
     */
    public boolean isOpen() {
        Instant now = Instant.now();
        return now.isAfter(this.opensAt) && now.isBefore(this.closesAt);
    }
}