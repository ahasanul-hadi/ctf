package com.cirt.ctf.marking;

import com.cirt.ctf.submission.SubmissionEntity;
import com.cirt.ctf.user.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "results")
@Data
public class ResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "submission_id")
    private SubmissionEntity submission;

    private int score;

    @ManyToOne
    @JoinColumn(name = "examiner_id")
    private User examiner;

    @CreationTimestamp
    @Column(name="marking_time", nullable = false)
    private LocalDateTime markingTime;

}
