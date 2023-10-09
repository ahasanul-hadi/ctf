package com.cirt.ctf.team;

import com.cirt.ctf.email.MailDTO;
import com.cirt.ctf.email.MailService;
import com.cirt.ctf.enums.Role;
import com.cirt.ctf.payload.*;
import com.cirt.ctf.user.UserDTO;
import com.cirt.ctf.user.UserService;
import com.cirt.ctf.util.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final ModelMapper modelMapper;
    private final TeamRepository teamRepository;
    private final UserService userService;
    private final MailService emailer;

    @Transactional
    public TeamEntity addTeam(TeamDTO teamDTO, TeamLeader leader, Member1 member1, Member2 member2, Member3 member3, Member4 member4, Member5 member5) {
        TeamEntity team = modelMapper.map(teamDTO, TeamEntity.class);
        team = teamRepository.save(team);


        //Team Leader
        leader.setPassword(Utils.getRandomPassword(6));
        UserDTO teamLeader = UserDTO.builder().team(team).role(Role.TEAM_LEAD).email(leader.getEmail()).name(leader.getName()).mobile(leader.getMobile()).password(leader.getPassword()).designation(leader.getDesignation()).build();
        userService.saveUser(teamLeader, leader.getFile());


        //member1
        member1.setPassword(Utils.getRandomPassword(6));
        UserDTO user1 = UserDTO.builder().team(team).role(Role.USER).email(member1.getEmail1()).name(member1.getName1()).mobile(member1.getMobile1()).password(member1.getPassword()).designation(member1.getDesignation1()).build();
        userService.saveUser(user1, member1.getFile1());

        //member2
        member2.setPassword(Utils.getRandomPassword(6));
        UserDTO user2 = UserDTO.builder().team(team).role(Role.USER).email(member2.getEmail2()).name(member2.getName2()).mobile(member2.getMobile2()).password(member2.getPassword()).designation(member2.getDesignation2()).build();
        userService.saveUser(user2, member2.getFile2());


        //member3
        member3.setPassword(Utils.getRandomPassword(6));
        UserDTO user3 = UserDTO.builder().team(team).role(Role.USER).email(member3.getEmail3()).name(member3.getName3()).mobile(member3.getMobile3()).password(member3.getPassword()).designation(member3.getDesignation3()).build();
        userService.saveUser(user3, member3.getFile3());


        //member4
        if (member4.getEmail4() != null && !member4.getEmail4().isEmpty()) {
            member4.setPassword(Utils.getRandomPassword(6));
            UserDTO user4 = UserDTO.builder().team(team).role(Role.USER).email(member4.getEmail4()).name(member4.getName4()).mobile(member4.getMobile4()).password(member4.getPassword()).designation(member4.getDesignation4()).build();
            userService.saveUser(user4, member4.getFile4());
        }

        //member5
        if (member5.getEmail5() != null && !member5.getEmail5().isEmpty()) {
            member5.setPassword(Utils.getRandomPassword(6));
            UserDTO user5 = UserDTO.builder().team(team).role(Role.USER).email(member5.getEmail5()).name(member5.getName5()).mobile(member5.getMobile5()).password(member5.getPassword()).designation(member5.getDesignation5()).build();
            userService.saveUser(user5, member5.getFile5());
        }

        MailDTO mailDTO= MailDTO.builder().from("CIRT CTF").subject("User Registration").build();
        //emailer.send();


        team = teamRepository.findById(team.getId()).orElse(null);

        return team;
    }


    public List<TeamDTO> getTeams(){
        return teamRepository.findAll().stream().map(team->modelMapper.map(team, TeamDTO.class)).toList();
    }


}
