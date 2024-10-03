package com.cirt.ctf.challenge;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import com.cirt.ctf.hints.HintsEntity;
import com.cirt.ctf.marking.ResultEntity;
import com.cirt.ctf.submission.SubmissionEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import com.cirt.ctf.team.TeamEntity;
import com.cirt.ctf.user.User;

import lombok.Data;

@Entity
@Table(name = "challenges")
@Data
public class ChallengeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "category", nullable = false, length = 20)
    private String category;
    @Column(name = "total_mark", nullable = false)
    private int totalMark;
    @Column(name = "attempts", nullable = false)
    private int attempts;
    @Column(name = "marking_type", nullable = false, length=20)
    private String markingType;
    @Column(name = "title", nullable = false, length = 50)
    private String title;
    @Column(name = "description", nullable = false, length = 65535)
    private String description;
    @Column(name = "attachment", nullable = true, length = 256)
    private String attachment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "challenge", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<SubmissionEntity> submissions;

    @Column(name = "is_scoreboard_published")
    private boolean isScoreboardPublished;

    @Column(name="deadline", nullable = false)
    private LocalDateTime deadline;

    @Column(name = "visibility", nullable = false, length=10)
    private String visibility;

    @Column(name = "answer", nullable = true, length=255)
    private String answer;


    @OneToOne(mappedBy = "challenge", optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private HintsEntity hint;

    @CreationTimestamp(source = SourceType.DB)
    private Instant createdAt;

    @UpdateTimestamp(source = SourceType.DB)
    private Instant updatedAt;
}