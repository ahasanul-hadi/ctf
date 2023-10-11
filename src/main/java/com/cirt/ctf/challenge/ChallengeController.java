package com.cirt.ctf.challenge;

import java.security.Principal;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cirt.ctf.enums.Category;
import com.cirt.ctf.enums.Role;
import com.cirt.ctf.team.TeamEntity;
import com.cirt.ctf.user.UserDTO;

import jakarta.validation.Valid;
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
        // List<ChallengeDTO> dtos= this.challengeService.getChallengesForAdmin().stream()
        //                             .map(challenge -> modelMapper.map(challenge,ChallengeDTO.class)).toList();
        model.addAttribute("challenges", challengeService.getChallengesForAdmin());
        String htmlPage = role == "ADMIN"? "challenge/admin/home" : "challenge/user/home";
        return htmlPage;
    }

    @PostMapping
    public String submitForm(Model model, @ModelAttribute("challenge") ChallengeDTO challengeDTO, BindingResult result, final RedirectAttributes redirectAttributes) {
        System.out.println(challengeDTO.getDeadline());
        this.challengeService.saveChallenge(challengeDTO);
        redirectAttributes.addFlashAttribute("message", "Challenge is successfully saved");
        redirectAttributes.addFlashAttribute("type", "success");
        return "redirect:/challenges";
    }
    @GetMapping("/add" )
    public String getAddChallengePage(Model model, Principal principal) {
        String role = "ADMIN";
        model.addAttribute("categories", Category.values());
        ChallengeDTO challengeDTO = new ChallengeDTO();
        challengeDTO.setTitle("Challenge ");
        challengeDTO.setTotalMark(150);
        challengeDTO.setMarkingType("manual");
        challengeDTO.setVisibility("hidden");
        challengeDTO.setAttempts(1);
        model.addAttribute("challenge", challengeDTO);
        if(role != Role.ADMIN.toString()) {
            return "redirect:/challenges";
        }
        return "challenge/admin/add";
    }
    
}
