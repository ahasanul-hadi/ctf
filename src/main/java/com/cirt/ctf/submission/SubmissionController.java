package com.cirt.ctf.submission;

import com.cirt.ctf.challenge.ChallengeService;
import com.cirt.ctf.document.DocumentService;
import com.cirt.ctf.marking.ResultDTO;
import com.cirt.ctf.payload.TeamRegistration;
import com.cirt.ctf.user.User;
import com.cirt.ctf.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Date;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/submissions")
public class SubmissionController {
    private final ModelMapper modelMapper;
    private final SubmissionService submissionService;
    private final DocumentService documentService;
    private final UserService userService;
    private final ChallengeService challengeService;

    @GetMapping
    public String getSubmissions(Model model){
        model.addAttribute("submissions",submissionService.findAll());
        return "submission/submission";
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/mark/{id}")
    public String getSubmission(@PathVariable("id") Long id,  Model model){

        SubmissionDTO submissionDTO= submissionService.findById(id);

        model.addAttribute("resultDTO", new ResultDTO());
        model.addAttribute("submission",submissionDTO);
        model.addAttribute("document",documentService.findById(submissionDTO.getDocumentID()));
        return "marking/mark";
    }

    @PostMapping
    public String saveSubmission(@Valid @ModelAttribute("submission") SubmissionDTO submissionDTO, BindingResult result, Model model, Principal principal, final RedirectAttributes redirectAttributes)
    {
        SubmissionEntity returnedEntity = null;
        User user= userService.findUserByEmail(principal.getName()).orElseThrow();
        String role = user.getRole().toString();
        submissionDTO.setSolver(user);
        submissionDTO.setTeam(user.getTeam());
        submissionDTO.setSubmissionTime( LocalDateTime.now() );
        submissionDTO.setChallenge(challengeService.getChallengeById(submissionDTO.getChallengeID()));
        returnedEntity = submissionService.createSubmission(submissionDTO);
        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("message", "Submission is Recorded.");
        return "redirect:/submissions";
    }
}
