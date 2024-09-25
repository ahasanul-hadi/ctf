package com.cirt.ctf.team;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cirt.ctf.submission.SubmissionEntity;

@Repository
public interface TeamRepository extends JpaRepository<TeamEntity, Long> {
    List<TeamEntity> findByTeamNameNotContaining(String adminTeamName);

    @Query(value = "Select t from TeamEntity t LEFT JOIN fetch t.submissions s LEFT JOIN FETCH s.result r where t.id In :ids")
    List<TeamEntity> findByIdIn(List<Long> ids);


}
