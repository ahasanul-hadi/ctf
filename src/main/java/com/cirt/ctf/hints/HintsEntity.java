package com.cirt.ctf.hints;

import com.cirt.ctf.challenge.ChallengeEntity;
import com.cirt.ctf.submission.SubmissionEntity;
import com.cirt.ctf.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "hints")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HintsEntity {
    @Id
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "challenge_id")
    private ChallengeEntity challenge;

    @Column(name = "deduct_mark")
    private Integer deductMark;

    @Column(name = "description")
    private String description;

    @Column(name = "show_hint", columnDefinition = "boolean default false")
    private boolean showHint;

}
