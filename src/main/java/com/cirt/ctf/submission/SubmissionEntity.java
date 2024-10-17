package com.cirt.ctf.submission;

import com.cirt.ctf.challenge.ChallengeEntity;
import com.cirt.ctf.marking.ResultEntity;
import com.cirt.ctf.team.TeamEntity;
import com.cirt.ctf.user.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Data
public class SubmissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="submission_time", nullable = false)
    private LocalDateTime submissionTime;

    @Column(name = "document_id")
    private String documentID;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "challenge_id", referencedColumnName = "id")
    private ChallengeEntity challenge;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private TeamEntity team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitter_id", referencedColumnName = "id")
    private User solver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taker_id", referencedColumnName = "id")
    private User takenBy;

    @Column(name = "is_published")
    private boolean isPublished=false;

    @Column(name = "mark")
    private Integer mark;

    @Column(name = "penalty")
    private Integer penalty;

    @Column(name = "score")
    private Integer score;

    @OneToOne(mappedBy = "submission",  cascade = CascadeType.ALL, orphanRemoval = true)
    private ResultEntity result;



}
