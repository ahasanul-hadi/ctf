package com.cirt.ctf.team;

import com.cirt.ctf.submission.SubmissionEntity;
import com.cirt.ctf.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "teams")
@Data
public class TeamEntity {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @Column(name = "team_organization", nullable = false)
    private String teamOrganization;

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderID;

    @Column(name = "payment_email", nullable = false, unique = true)
    private String paymentEmail;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "team", fetch = FetchType.LAZY)
    private List<User> members;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private User approvedBy;

    @Column(name = "is_displayed", columnDefinition = "boolean default true")
    private boolean isDisplayed;

    @Column(name = "approve_date")
    private LocalDateTime approveDate;

    @OneToMany(mappedBy = "team")
    private List<SubmissionEntity> submissions;

    public void addMember(User member) {
        members.add(member);
        member.setTeam(this);
    }

    public void removeMember(User member) {
        members.remove(member);
        member.assignTeam(null);
    }
}
