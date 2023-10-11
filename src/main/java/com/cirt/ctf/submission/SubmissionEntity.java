package com.cirt.ctf.submission;

import com.cirt.ctf.challenge.ChallengeEntity;
import com.cirt.ctf.marking.ResultEntity;
import com.cirt.ctf.team.TeamEntity;
import com.cirt.ctf.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
public class SubmissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @CreationTimestamp
    @Column(name="submission_time", nullable = false)
    private LocalDateTime submissionTime;

    @Column(name = "file_path", nullable = false, length = 256)
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "challenge_id", referencedColumnName = "id")
    private ChallengeEntity challenge;

    @ManyToOne
    @JoinColumn(name = "submitter_id", referencedColumnName = "id")
    private User solver;

    @ManyToOne
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private TeamEntity team;

    @OneToOne(mappedBy = "submission", orphanRemoval = true)
    public ResultEntity result;


}
