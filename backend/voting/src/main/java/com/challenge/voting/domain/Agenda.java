package com.challenge.voting.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Domain model representing a voting agenda.
 * An agenda is a proposal or topic that associates vote on.
 * Each agenda can have only one voting session associated with it.
 * 
 * <p>Attributes:
 * <ul>
 *   <li>id: Unique identifier (auto-generated)</li>
 *   <li>title: Agenda title (max 150 chars, required)</li>
 *   <li>description: Agenda description (max 500 chars, required)</li>
 * </ul>
 * 
 * @author Voting Challenge API
 * @version 1.0.0
 */
@Entity
@Table(name = "agendas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;
}