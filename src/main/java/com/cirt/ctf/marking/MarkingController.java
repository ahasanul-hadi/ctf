package com.cirt.ctf.marking;

import com.cirt.ctf.challenge.ChallengeDTO;
import com.cirt.ctf.challenge.ChallengeEntity;
import com.cirt.ctf.challenge.ChallengeService;
import com.cirt.ctf.exception.ApplicationException;
import com.cirt.ctf.submission.SubmissionDTO;
import com.cirt.ctf.submission.SubmissionEntity;
import com.cirt.ctf.submission.SubmissionService;
import com.cirt.ctf.user.User;
import com.cirt.ctf.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@RequestMapping("/marking")
//@Secured("ADMIN")
public class MarkingController {
    private final SubmissionService submissionService;
    private  final UserService userService;
    private final ChallengeService challengeService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public String mark(@Valid ResultDTO resultDTO, Principal principal, final RedirectAttributes redirectAttributes){

        SubmissionDTO submissionDTO= submissionService.findById(resultDTO.getSubmissionID());
        if(submissionDTO.getResult()!=null){
            redirectAttributes.addFlashAttribute("type", "error");
            redirectAttributes.addFlashAttribute("message", "This submission is already assessed by <b>"+submissionDTO.getResult().getExaminer().getName()+"</b>");
            return "redirect:/submissions";
        }

        User admin= userService.findUserByEmail(principal.getName()).orElseThrow();
        resultDTO.setExaminer(admin);
        resultDTO.setMarkingTime(LocalDateTime.now());

        submissionService.giveMark(resultDTO);
        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("message", "Score has been given for the submission");
        return "redirect:/submissions";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER')")
    @GetMapping("/submissions/{id}")
    public String getMarkingPage(@PathVariable("id") Long submissionID, Model model, Principal principal, final RedirectAttributes redirectAttributes) throws ApplicationException {

        User admin= userService.findUserByEmail(principal.getName()).orElseThrow();
        User takenBy= submissionService.bookExaminer(submissionID, admin);
        if(!Objects.equals(takenBy.getId(), admin.getId())){
            redirectAttributes.addFlashAttribute("type", "error");
            redirectAttributes.addFlashAttribute("message", "This submission is already taken by another admin:"+takenBy.getName());
            return "redirect:/submissions";
        }

        SubmissionDTO submissionDTO= submissionService.findById(submissionID);

        model.addAttribute("resultDTO", new ResultDTO());
        model.addAttribute("submission",submissionDTO);
        return "marking/mark";
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER')")
    @GetMapping("/challenges/{id}")
    public String getMarkingOfChallenge(@PathVariable("id") Long challengeID, Model model, Principal principal, final RedirectAttributes redirectAttributes) throws ApplicationException {

        User admin= userService.findUserByEmail(principal.getName()).orElseThrow();
        ChallengeEntity challenge= challengeService.findByID(challengeID);
        List<SubmissionEntity> submissions= submissionService.findByChallenges(challengeID);

        model.addAttribute("challenge",challenge);
        model.addAttribute("submissions",submissions);
        return "marking/result-preview";
    }
}
