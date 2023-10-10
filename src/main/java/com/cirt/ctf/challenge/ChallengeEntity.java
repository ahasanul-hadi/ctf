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
    @Column(name = "manual_marking", nullable = false)
    private boolean manualMarking;
    @Column(name = "title", nullable = false, length = 50)
    private String title;
    @Column(name = "description", nullable = false, length = 1024)
    private String description;
    @Column(name = "file_path", nullable = false, length = 256)
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private User user;

    @Column(name="deadline", nullable = false)
    private Instant deadline;
    
    @CreationTimestamp(source = SourceType.DB)
    private Instant createdAt;

    @UpdateTimestamp(source = SourceType.DB)
    private Instant updatedAt;
}