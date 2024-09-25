package com.cirt.ctf.submission;

import com.cirt.ctf.scoreboard.ScoreSummary;
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

    List<SubmissionEntity> findByChallengeId(Long challengeID);

    @Query(value = """
            SELECT t.id as id, t.team_name as teamName, t.team_organization as teamOrganization, sum(r.score) as score, max(submission_time) as maxSubmissionTime 
            FROM teams t LEFT JOIN  submissions s ON s.team_id=t.id AND s.is_published=true
            LEFT JOIN results r ON r.submission_id= s.id where t.is_displayed = true
            GROUP BY t.id ORDER BY score DESC, maxSubmissionTime ASC, id ASC
            """,nativeQuery = true)
    List<ScoreSummary> getScoreboardV2();
}
