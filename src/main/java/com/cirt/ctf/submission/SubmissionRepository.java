package com.cirt.ctf.submission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<SubmissionEntity,Long> {
    @Query("SELECT s FROM SubmissionEntity s WHERE s.team.id=?1" )
    List<SubmissionEntity> findByTeam(Long teamID);

    @Query("SELECT s FROM SubmissionEntity s WHERE s.team.id=?1 and s.challenge.id=?2" )
    List<SubmissionEntity> getSubmissionListByChallengeAndTeam(Long teamID, Long challengeID);
}
