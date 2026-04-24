package com.challenge.voting.domain;

import com.challenge.voting.domain.enums.VoteChoice;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "votes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_vote_session_cpf", columnNames = { "voting_session_id", "associate_cpf" })
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