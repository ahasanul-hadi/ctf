package com.cirt.ctf.submission;

import com.cirt.ctf.challenge.AutoAnswerControllerService;
import com.cirt.ctf.challenge.AutoAnswerEntity;
import com.cirt.ctf.challenge.ChallengeDTO;
import com.cirt.ctf.challenge.ChallengeEntity;
import com.cirt.ctf.challenge.ChallengeService;
import com.cirt.ctf.document.DocumentService;
import com.cirt.ctf.enums.Role;
import com.cirt.ctf.marking.ResultDTO;
import com.cirt.ctf.payload.TeamRegistration;
import com.cirt.ctf.user.User;
import com.cirt.ctf.user.UserService;

import com.cirt.ctf.util.Utils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private final AutoAnswerControllerService autoAnswerService;
    @GetMapping
    public String getSubmissions(Model model, Principal principal){
        User user= userService.findUserByEmail(principal.getName()).orElseThrow();

        if(user.getRole()==Role.ADMIN)
            model.addAttribute("submissions",submissionService.findAllSubmissions());
        else
            model.addAttribute("submissions",submissionService.findByTeam(user.getTeam().getId()));
        return "submission/submission";
    }


    @PostMapping
    public String saveSubmission(@Valid @ModelAttribute("submission") SubmissionDTO submissionDTO, BindingResult result, Model model, Principal principal, final RedirectAttributes redirectAttributes)
    {

        User user= userService.findUserByEmail(principal.getName()).orElseThrow();
        String role = user.getRole().toString();
        ChallengeEntity challengeEntity = challengeService.getChallengeById(submissionDTO.getChallengeID());
        int submissionCount = submissionService.getSubmissionCount(user.getTeam().getId(), submissionDTO.getChallengeID());
        LocalDateTime deadline = challengeEntity.getDeadline();
        String message = Utils.validatePDFFile(submissionDTO.getFile());

        if(deadline.isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("type", "error");
            redirectAttributes.addFlashAttribute("message", "Submission Time is over.");
            return "redirect:/challenges";
        }
        if(challengeEntity.getAttempts() <= submissionCount) {
            redirectAttributes.addFlashAttribute("type", "error");
            redirectAttributes.addFlashAttribute("message", "Submission Attempt is over.");
            return "redirect:/challenges";
        }
        if(challengeEntity.getMarkingType().equals("manual") && message != null) {
            System.out.println(message);
            redirectAttributes.addFlashAttribute("type", "error");
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/challenges";
        }
        if(challengeEntity.getMarkingType().equals("auto") && 
            submissionService.anySubmissionAccepted(user.getTeam().getId(), submissionDTO.getChallengeID())) {
            
            redirectAttributes.addFlashAttribute("type", "error");
            redirectAttributes.addFlashAttribute("message", "Submission is already ACCEPTED for this challenge. No further submission allowed.");
            return "redirect:/challenges";
        }
        submissionDTO.setSolver(user);
        submissionDTO.setTeam(user.getTeam());
        submissionDTO.setSubmissionTime( LocalDateTime.now() );
        submissionDTO.setChallenge(challengeEntity);
        submissionDTO.setMarkingType(challengeEntity.getMarkingType());

        // check for auto/manual marking type

        SubmissionEntity submissionEntity = submissionService.createSubmission(submissionDTO);

        if(challengeEntity.getMarkingType().equals("auto")) {
            ResultDTO resultDTO = new ResultDTO();
            resultDTO.setMarkingTime(LocalDateTime.now());
            Long teamId = user.getTeam().getId();
            String answer = autoAnswerService.getAutoAnswerForJudge(challengeEntity.getId(), teamId);
            String submittedAnswer = submissionDTO.getDocumentID();     // for auto type, doc ID holds the
            String verdict = generateAutoVerdict(answer, submittedAnswer);
            resultDTO.setComments(verdict);
            if(verdict.equals("ACCEPTED")) {
                resultDTO.setScore(challengeEntity.getTotalMark());
            } else {
                resultDTO.setScore(0);
            }
            resultDTO.setSubmissionID(submissionEntity.getId());
            submissionService.giveMark(resultDTO);
        }
        
        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("message", "Submission is Recorded.");
        return "redirect:/submissions";
    }

    private String generateAutoVerdict(String realAnswer, String submittedAnswer) {
        String verdictToReturn = realAnswer.trim().compareToIgnoreCase(submittedAnswer.trim()) == 0 ? "ACCEPTED" : "WRONG ANSWER";
        return verdictToReturn;
    }
}
