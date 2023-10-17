package com.cirt.ctf.scoreboard;

import com.cirt.ctf.challenge.ChallengeEntity;
import com.cirt.ctf.challenge.ChallengeService;
import com.cirt.ctf.submission.SubmissionEntity;
import com.cirt.ctf.team.TeamDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/scoreboard")
@RequiredArgsConstructor
public class ScoreBoardController {

    private final ScoreBoardService scoreBoardService;
    private final ChallengeService challengeService;


    @GetMapping
    public String getScoreboard(Model model){
        model.addAttribute("scoreboard",scoreBoardService.getScoreboard());
        model.addAttribute("top10",scoreBoardService.getTop10());
        return "scoreboard/scoreboard";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/publish")
    public String getPublish(Model model){
        model.addAttribute("publishList",challengeService.findAll());
        return "scoreboard/publish";
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/publish/challenge/{id}")
    public String publish(@PathVariable("id") Long id, RedirectAttributes redirectAttributes){

        ChallengeEntity challengeEntity = challengeService.findByID(id);
        List<SubmissionEntity> submissionEntityList= challengeEntity.getSubmissions();
        for(SubmissionEntity submissionEntity: submissionEntityList){
            if(submissionEntity.getResult()==null){
                redirectAttributes.addFlashAttribute("type", "error");
                redirectAttributes.addFlashAttribute("message", "All submissions for "+challengeEntity.getTitle()+" has not been assessed yet!");
                return "redirect:/submissions";
            }
        }

        scoreBoardService.publish(challengeEntity);

        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("message", "Scoreboard has been updated!");
        return "redirect:/scoreboard";
    }


}
