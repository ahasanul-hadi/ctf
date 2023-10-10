package com.cirt.ctf.challenge;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cirt.ctf.team.TeamEntity;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/challenges")
@RequiredArgsConstructor
public class ChallengeController{
    private final ChallengeService challengeService;
    private final ModelMapper modelMapper;

    @GetMapping
    public String getChallengesPage(Model model) {
        String role = "ADMIN";
        
        
        String htmlPage = role == "ADMIN"? "challenge/admin/home" : "challenge/user/home";
        return htmlPage;
    }
    
}
