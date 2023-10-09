package com.cirt.ctf.team;

import com.cirt.ctf.payload.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamRepository teamRepository;
    private final TeamService teamService;

    @PostMapping()
    public String addTeam(TeamDTO teamDTO, TeamLeader leader, Member1 member1, Member2 member2, Member3 member3, Member4 member4, Member5 member5, Model model, HttpServletRequest request){
        TeamEntity team = teamService.addTeam(teamDTO,leader, member1, member2, member3, member4, member5);
        return "team/teams";
    }


    @GetMapping()
    public String getTeams( Model model, HttpServletRequest request){
        model.addAttribute("teams",teamService.getTeams());
        return "team/teams";
    }

    @GetMapping("/registration")
    public String getRegistration( Model model, HttpServletRequest request){
        model.addAttribute("teams",teamService.getTeams());
        return "team/registration";
    }

}
