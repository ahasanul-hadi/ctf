package com.cirt.ctf.hints;

import com.cirt.ctf.challenge.ChallengeEntity;
import com.cirt.ctf.challenge.ChallengeService;
import com.cirt.ctf.exception.ApplicationException;
import com.cirt.ctf.submission.SubmissionDTO;
import com.cirt.ctf.submission.SubmissionService;
import com.cirt.ctf.user.User;
import com.cirt.ctf.user.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/hints")
//@Secured("ADMIN")
public class HintsController {
    private final HintsService hintsService;
    private  final UserService userService;
    private final SubmissionService submissionService;



    @GetMapping("/fetch/{id}")
    @Transactional
    public String fetchHintById(@PathVariable("id") Long hintID, Model model, Principal principal, final RedirectAttributes redirectAttributes) throws ApplicationException {

        User requester= userService.findUserByEmail(principal.getName()).orElseThrow();
        HintsEntity hint= hintsService.getHintById(hintID);

        Optional<TeamHintsEntity> teamHint= hintsService.getTeamHint(requester.getTeam().getId(), hintID);
        if(teamHint.isEmpty()) {
            hintsService.requestHint(requester.getTeam().getId(), hintID, requester.getId());
            submissionService.submitPenalty(requester.getTeam().getId(), requester, hint);
        }

        model.addAttribute("hint",hint);
        return "hint/hint-preview";
    }
}
