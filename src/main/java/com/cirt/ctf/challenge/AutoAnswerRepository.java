package com.cirt.ctf.challenge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoAnswerRepository extends JpaRepository<AutoAnswerEntity, Long> {

    public AutoAnswerEntity findByTeamIdAndChallengeId(Long teamId, Long challengeId);
    
}
