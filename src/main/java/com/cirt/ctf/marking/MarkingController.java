package com.cirt.ctf.marking;

import com.cirt.ctf.submission.SubmissionDTO;
import com.cirt.ctf.submission.SubmissionEntity;
import com.cirt.ctf.submission.SubmissionService;
import com.cirt.ctf.user.User;
import com.cirt.ctf.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/marking")
public class MarkingController {
    private final SubmissionService submissionService;
    private  final UserService userService;

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
}
