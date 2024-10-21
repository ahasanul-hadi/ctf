package com.cirt.ctf.submission;

import com.cirt.ctf.scoreboard.ScoreSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<SubmissionEntity,Long> {
    @Query("SELECT s FROM SubmissionEntity s WHERE s.team.id=?1" )
    List<SubmissionEntity> findByTeam(Long teamID);

    @Query("SELECT s FROM SubmissionEntity s LEFT JOIN FETCH s.challenge c LEFT JOIN FETCH s.team t LEFT JOIN FETCH s.result r LEFT JOIN FETCH s.solver solver LEFT JOIN FETCH s.takenBy examiner" )
    List<SubmissionEntity> findAllSubmissions();

    @Query("SELECT s FROM SubmissionEntity s WHERE s.team.id=?1 and s.challenge.id=?2" )
    List<SubmissionEntity> getSubmissionListByChallengeAndTeam(Long teamID, Long challengeID);

    @Query("SELECT s FROM SubmissionEntity s WHERE s.team.id=?1 and s.challenge.id=?2" )
    Optional<SubmissionEntity> getSubmissionByTeamAndChallenge(Long teamID, Long challengeID);

    @Query("SELECT s FROM SubmissionEntity s LEFT JOIN FETCH s.challenge c LEFT JOIN FETCH s.team t LEFT JOIN fetch s.solver solver LEFT JOIN fetch s.takenBy examiner WHERE s.challenge.id=?1" )
    List<SubmissionEntity> findByChallengeId(Long challengeID);

    @Query(value = """
            SELECT t.id as id, t.team_name as teamName, t.team_organization as teamOrganization, sum(IFNULL(s.score,0)) as score, max(submission_time) as maxSubmissionTime 
            FROM teams t LEFT JOIN  submissions s ON s.team_id=t.id AND s.is_published=true AND s.score != 0
            where t.is_displayed = true
            GROUP BY t.id ORDER BY score DESC, maxSubmissionTime ASC, id ASC
            """,nativeQuery = true)
    List<ScoreSummary> getScoreboardV2();
}
