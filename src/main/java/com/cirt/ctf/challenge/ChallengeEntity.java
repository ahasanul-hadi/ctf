package com.cirt.ctf.challenge;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import com.cirt.ctf.team.TeamEntity;
import com.cirt.ctf.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    @Column(name = "is_manual", nullable = false)
    private boolean isManual;
    @Column(name = "title", nullable = false, length = 50)
    private String title;
    @Column(name = "description", nullable = false, length = 65535)
    private String description;
    @Column(name = "file_path", nullable = true, length = 256)
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private User user;

    @Column(name="deadline", nullable = false)
    private Instant deadline;

    @Column(name = "visibility", nullable = false, length=10)
    private String visibility;

    @CreationTimestamp(source = SourceType.DB)
    private Instant createdAt;

    @UpdateTimestamp(source = SourceType.DB)
    private Instant updatedAt;
}