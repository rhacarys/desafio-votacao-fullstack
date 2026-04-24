package com.challenge.voting.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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
    private LocalDateTime opensAt;

    @Column(name = "closes_at", nullable = false)
    private LocalDateTime closesAt;

    /**
     * Rich Domain Model.
     * 
     * @return true if the voting session is currently open, false otherwise.
     */
    public boolean isOpen() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(this.opensAt) && now.isBefore(this.closesAt);
    }
}