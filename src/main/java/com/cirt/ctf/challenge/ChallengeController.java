package com.cirt.ctf.challenge;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.cirt.ctf.user.User;
import com.cirt.ctf.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cirt.ctf.enums.Category;
import com.cirt.ctf.enums.Role;
import com.cirt.ctf.submission.SubmissionDTO;
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
    private final UserService userService;

    @GetMapping
    public String getChallengesPage(Model model, Principal principal) {
        User user= userService.findUserByEmail(principal.getName()).orElseThrow();

        String role = user.getRole().toString();
        // List<ChallengeDTO> dtos= this.challengeService.getChallengesForAdmin().stream()
        //                             .map(challenge -> modelMapper.map(challenge,ChallengeDTO.class)).toList();
        if(role == "ADMIN")
            model.addAttribute("challenges", challengeService.getChallengesForAdmin());
        else {
            model.addAttribute("challenges", challengeService.getChallengesForUser());
            model.addAttribute("submission", new SubmissionDTO());
        }
            
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
        User user= userService.findUserByEmail(principal.getName()).orElseThrow();

        String role = user.getRole().toString();
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

    @GetMapping("/{id}")
    public String getSingleChallengePage(@PathVariable("id") Long id, Model model, Principal principal) {
        User user= userService.findUserByEmail(principal.getName()).orElseThrow();

        String role = user.getRole().toString();
        ChallengeEntity challengeEntity = challengeService.getChallengeById(id);
        ChallengeDTO challengeDTO = new ChallengeDTO();
        challengeDTO.setId(challengeEntity.getId());
        challengeDTO.setTitle(challengeEntity.getTitle());
        challengeDTO.setTotalMark(challengeEntity.getTotalMark());
        challengeDTO.setMarkingType(challengeEntity.getMarkingType());
        challengeDTO.setVisibility(challengeEntity.getVisibility());
        challengeDTO.setAttempts(challengeEntity.getAttempts());
        challengeDTO.setCategory(challengeEntity.getCategory());
        challengeDTO.setDescription(challengeEntity.getDescription());
        String[] deadlineTokens = challengeEntity.getDeadline().toString().split(":");
        String deadline = String.join(":", deadlineTokens[0], deadlineTokens[1]);
        challengeDTO.setDeadline(deadline);
        model.addAttribute("challenge", challengeDTO);
        model.addAttribute("categories", Category.values());
        if(role != Role.ADMIN.toString()) {
            return "redirect:/challenges";
        }
        return "challenge/admin/update";
    }

    @PostMapping("/{id}")
    public String updateChallenge(@PathVariable("id") Long id, Model model, @ModelAttribute("challenge") ChallengeDTO challengeDTO, Principal principal, RedirectAttributes redirectAttributes) {
        User user= userService.findUserByEmail(principal.getName()).orElseThrow();

        String role = user.getRole().toString();
        if(role == Role.ADMIN.toString()) {
            challengeService.updateChallenge(id, challengeDTO);
        }
        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("message", "Challenge successfully updated");
        return "redirect:/challenges";
    }
    
}
