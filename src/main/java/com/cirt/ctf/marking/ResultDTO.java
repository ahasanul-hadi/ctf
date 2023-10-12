package com.cirt.ctf.marking;

import com.cirt.ctf.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
public class ResultDTO {
    private long submissionID;
    private int score;
    private User examiner;
    private LocalDateTime markingTime;
    private String comments;
}
