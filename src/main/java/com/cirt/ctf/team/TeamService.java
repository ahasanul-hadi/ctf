package com.cirt.ctf.team;

import com.cirt.ctf.challenge.ChallengeService;
import com.cirt.ctf.email.MailDTO;
import com.cirt.ctf.email.MailService;
import com.cirt.ctf.enums.Role;
import com.cirt.ctf.payload.*;
import com.cirt.ctf.user.User;
import com.cirt.ctf.user.UserDTO;
import com.cirt.ctf.user.UserService;
import com.cirt.ctf.util.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final ModelMapper modelMapper;
    private final TeamRepository teamRepository;
    private final UserService userService;
    private final ChallengeService challengeService;

    public List<TeamDTO> findAll(){
        return teamRepository.findAll().stream().map(entity->modelMapper.map(entity,TeamDTO.class)).toList();
    }

    @Transactional
    public String addTeam(TeamRegistration teamDTO) {

        TeamDTO team = modelMapper.map(teamDTO, TeamDTO.class);
        TeamEntity entity=null;
        try {
            entity = save(team);
        }catch (Exception ee){
            System.out.println("inside main exp"+ee);
            return "Please provide unique payment email and order id";
        }

        //Team Leader
        teamDTO.setPassword(Utils.getRandomPassword(8));
        UserDTO teamLeader = UserDTO.builder().team(entity).role(Role.TEAM_LEAD).email(teamDTO.getEmail()).name(teamDTO.getName()).mobile(teamDTO.getMobile()).password(teamDTO.getPassword()).designation(teamDTO.getDesignation()).build();
        try {
            userService.saveUser(teamLeader, teamDTO.getFile());
        }catch (Exception e){
            return teamLeader.getEmail()+" is already used!";
        }


        //member1
        teamDTO.setPassword1(Utils.getRandomPassword(8));
        UserDTO user1 = UserDTO.builder().team(entity).role(Role.MEMBER).email(teamDTO.getEmail1()).name(teamDTO.getName1()).mobile(teamDTO.getMobile1()).password(teamDTO.getPassword()).designation(teamDTO.getDesignation1()).build();
        try {
            userService.saveUser(user1, teamDTO.getFile1());
        }catch (Exception e){
            return user1.getEmail()+" is already used!";
        }
        //member2
        teamDTO.setPassword2(Utils.getRandomPassword(8));
        UserDTO user2 = UserDTO.builder().team(entity).role(Role.MEMBER).email(teamDTO.getEmail2()).name(teamDTO.getName2()).mobile(teamDTO.getMobile2()).password(teamDTO.getPassword()).designation(teamDTO.getDesignation2()).build();
        try {
            userService.saveUser(user2, teamDTO.getFile2());
        }catch (Exception e){
            return user2.getEmail()+" is already used!";
        }

        //member3
        teamDTO.setPassword3(Utils.getRandomPassword(8));
        UserDTO user3 = UserDTO.builder().team( entity ).role(Role.MEMBER).email(teamDTO.getEmail3()).name(teamDTO.getName3()).mobile(teamDTO.getMobile3()).password(teamDTO.getPassword()).designation(teamDTO.getDesignation3()).build();
        try {
            userService.saveUser(user3, teamDTO.getFile3());
        }catch (Exception e){
            return user3.getEmail()+" is already used!";
        }

        //member4
        if (teamDTO.getEmail4() != null && !teamDTO.getEmail4().isEmpty()) {
            teamDTO.setPassword4(Utils.getRandomPassword(8));
            UserDTO user4 = UserDTO.builder().team(entity).role(Role.MEMBER).email(teamDTO.getEmail4()).name(teamDTO.getName4()).mobile(teamDTO.getMobile4()).password(teamDTO.getPassword()).designation(teamDTO.getDesignation4()).build();
            userService.saveUser(user4, teamDTO.getFile4());
        }

        //member5
        if (teamDTO.getEmail5() != null && !teamDTO.getEmail5().isEmpty()) {
            teamDTO.setPassword5(Utils.getRandomPassword(8));
            UserDTO user5 = UserDTO.builder().team(entity).role(Role.MEMBER).email(teamDTO.getEmail5()).name(teamDTO.getName5()).mobile(teamDTO.getMobile5()).password(teamDTO.getPassword()).designation(teamDTO.getDesignation5()).build();
            userService.saveUser(user5, teamDTO.getFile5());
        }

        MailDTO mailDTO= MailDTO.builder().from("CIRT CTF").subject("User Registration").build();
        //emailer.send();


        entity = teamRepository.findById(team.getId()).orElse(null);

        return null;
    }

    public TeamEntity save(TeamDTO dto){
        TeamEntity entity= modelMapper.map(dto, TeamEntity.class);

        //setting random ID
        if(entity.getId()==null || entity.getId()==0){
            SecureRandom rand= new SecureRandom();
            entity.setId(Utils.generateRandomTeamID(12));
        }

        return teamRepository.save(entity);
    }

    public List<TeamDTO> getTeams(){
        return teamRepository.findAll().stream().map(team->modelMapper.map(team, TeamDTO.class)).toList();
    }


    public void approve(Long id, User admin) {
        TeamEntity team= teamRepository.findById(id).orElseThrow();
        team.setApprovedBy(admin);
        team.setApproveDate(LocalDateTime.now());
        teamRepository.save(team);


        team.getMembers().forEach(user->{
            user.setEnabled(true);
            userService.update(user);
        });

    }

    public TeamDTO findById(Long id) {
        TeamEntity team= teamRepository.findById(id).orElse(null);
        return team==null?null:modelMapper.map(team, TeamDTO.class);
    }
    public TeamEntity findEntityById(Long id){
        return teamRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        teamRepository.deleteById(id);
    }

    public long getSolvedCount(TeamDTO dto){
        return dto.getSubmissions().stream().filter(sub->sub.isPublished() && sub.getResult().getScore()>0).count();
    }

    public long getFailedCount(TeamDTO dto){
        return (long) challengeService.findAll().size() - getSolvedCount(dto);
    }
}
