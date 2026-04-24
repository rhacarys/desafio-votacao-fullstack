package com.challenge.voting.repository;

import com.challenge.voting.domain.Vote;
import com.challenge.voting.domain.enums.VoteChoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByVotingSessionIdAndAssociateCpf(Long votingSessionId, String associateCpf);

    long countByVotingSessionIdAndChoice(Long votingSessionId, VoteChoice choice);
}
