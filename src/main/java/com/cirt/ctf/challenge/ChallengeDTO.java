package com.cirt.ctf.challenge;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import com.cirt.ctf.submission.SubmissionEntity;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import com.cirt.ctf.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChallengeDTO {
    private long id;

    @NotEmpty(message = "Category cannot be empty")
    private String category;
    @NotEmpty(message = "Total mark cannot be empty")
    private int totalMark;
    @NotEmpty(message = "Attempts cannot be empty")
    private int attempts;
    @NotEmpty(message = "Marking type cannot be empty")
    private String markingType;
    @NotEmpty(message = "Title cannot be empty")
    private String title;
    @NotEmpty(message = "Description cannot be empty")
    private String description;
    private String filePath;

    @NotEmpty(message = "Submission deadline cannot be empty")
    private String deadline;

    @NotEmpty(message = "Visibility cannot be empty")
    private String visibility;

    private List<SubmissionEntity> submissions;
    private boolean isScoreboardPublished;
    private int attemptsDone;
    private String attemptStatus;
}
