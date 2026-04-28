package com.challenge.voting.domain;

import com.challenge.voting.domain.enums.VoteChoice;
import jakarta.persistence.*;
import lombok.*;

/**
 * Domain model representing a vote cast by an associate in a voting session.
 * Each vote records the choice (YES/NO) made by an associate identified by their CPF.
 * Enforces data integrity through a unique composite index on (voting_session_id, associate_cpf)
 * to prevent duplicate votes from the same associate in the same session.
 * 
 * <p>Attributes:
 * <ul>
 *   <li>id: Unique identifier (auto-generated)</li>
 *   <li>votingSession: Reference to the voting session (required)</li>
 *   <li>associateCpf: CPF of the voting associate (11 digits, required)</li>
 *   <li>choice: The voting choice - YES or NO (required)</li>
 * </ul>
 * 
 * @author Voting Challenge API
 * @version 1.0.0
 */
@Entity
@Table(name = "votes", indexes = {
    @Index(name = "idx_vote_session_cpf", columnList = "voting_session_id, associate_cpf", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voting_session_id", nullable = false)
    private VotingSession votingSession;

    @Column(name = "associate_cpf", nullable = false, length = 11)
    private String associateCpf;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteChoice choice;
}