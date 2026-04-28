package com.challenge.voting.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

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
     * Rich Domain Model.
     * 
     * @return true if the voting session is currently open, false otherwise.
     */
    public boolean isOpen() {
        Instant now = Instant.now();
        return now.isAfter(this.opensAt) && now.isBefore(this.closesAt);
    }
}