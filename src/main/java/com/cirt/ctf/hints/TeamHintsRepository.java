package com.cirt.ctf.hints;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamHintsRepository extends JpaRepository<TeamHintsEntity, Long> {
    Optional<TeamHintsEntity> findByTeamIdAndHintId(Long teamID, Long hintID);
}
