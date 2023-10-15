package com.cirt.ctf.user;

import com.cirt.ctf.enums.Role;
import com.cirt.ctf.submission.SubmissionEntity;
import com.cirt.ctf.team.TeamEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails, CredentialsContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String mobile;

    @Column(name = "education")
    private String education;

    @Column(name="address")
    private String address;

    @Column(name="personal_info")
    private String personalInfo;

    @Column(name="designation")
    private String designation;

    @Column(name="avatar_id")
    private String avatarID;

    @ManyToOne
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private TeamEntity team;

    @OneToMany(mappedBy = "solver", orphanRemoval = true)
    private List<SubmissionEntity> submissions;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false,name = "role")
    private Role role;


    @Column(name="account_non_expired")
    private boolean accountNonExpired;

    @Column(name="account_non_locked")
    private boolean accountNonLocked;

    @Column(name="credentials_non_expired")
    private boolean credentialsNonExpired;

    private boolean enabled;

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Stream.of(role)
                .map(privilege -> new SimpleGrantedAuthority(privilege.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    public User(String name, String email, String mobile, String password, Role role) {
        this.name = name;
        this.email = email;
        this.mobile= mobile;
        this.password = password;
        this.role = role;
    }

    public void assignTeam(TeamEntity team){
        this.team=team;
    }

    @Transient
    private int score;
    public int getScore(){

        return submissions.stream().mapToInt(sub->{
                if(sub.getResult()==null)
                    return 0;
                else
                    return sub.getResult().getScore();
            }).sum();
    }
}
