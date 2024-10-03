package com.cirt.ctf.hints;

import com.cirt.ctf.team.TeamEntity;
import com.cirt.ctf.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "team_hints_request")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamHintsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private TeamEntity team;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private HintsEntity hint;

    @Column(name="penalty")
    private Integer penalty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private User requester;

    @CreationTimestamp
    @Column(name="request_time", nullable = false)
    private LocalDateTime requestTime;


}
