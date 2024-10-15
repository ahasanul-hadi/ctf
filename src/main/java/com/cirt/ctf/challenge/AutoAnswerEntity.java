package com.cirt.ctf.challenge;

import java.util.List;

import com.cirt.ctf.team.TeamEntity;
import com.cirt.ctf.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "teamwise_answer")
public class AutoAnswerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private Long teamId;

    @Column(nullable = false)
    private Long challengeId;

    @Column(name = "answer_text", nullable = false, length=255)
    private String answer;
}
