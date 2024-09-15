package com.cirt.ctf.team;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cirt.ctf.submission.SubmissionEntity;

@Repository
public interface TeamRepository extends JpaRepository<TeamEntity, Long> {
    List<TeamEntity> findByTeamNameNotContaining(String adminTeamName);
}
