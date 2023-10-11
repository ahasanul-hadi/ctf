package com.cirt.ctf.challenge;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeRepository extends JpaRepository<ChallengeEntity, Long>  {
    public List<ChallengeEntity> findByVisibility(String visibility);
}
