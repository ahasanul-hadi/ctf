package com.cirt.ctf.team;

import com.cirt.ctf.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "teams")
@Data
public class TeamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @Column(name = "team_organization", nullable = false)
    private String teamOrganization;

    @Column(name = "order_id", nullable = false)
    private String orderID;

    @Column(name = "payment_email", nullable = false)
    private String paymentEmail;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "team_id")
    List<User> members;

    public void addMember(User member) {
        members.add(member);
        member.setTeam(this);
    }

    public void removeMember(User member) {
        members.remove(member);
        member.assignTeam(null);
    }
}
