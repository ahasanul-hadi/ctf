package com.cirt.ctf.scoreboard;

import com.cirt.ctf.challenge.ChallengeEntity;
import com.cirt.ctf.challenge.ChallengeService;
import com.cirt.ctf.settings.SettingsEntity;
import com.cirt.ctf.settings.SettingsService;
import com.cirt.ctf.submission.SubmissionEntity;
import com.cirt.ctf.team.TeamDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/scoreboard")
@RequiredArgsConstructor
public class ScoreBoardController {

    private final ScoreBoardService scoreBoardService;
    private final ChallengeService challengeService;
    private final SettingsService settingsService;


    @GetMapping("/public")
    public String getPublicScoreboard(Model model){
        SettingsEntity settingsEntity = settingsService.findById(1L);
        if(settingsEntity.getScoreboardVisibility().equals("private")) {
            return "redirect:/login";
        } else {
            List<ScoreSummary> scoreList= scoreBoardService.getScoreboardV2();
            log.info("scoreList size:"+scoreList.size());
            model.addAttribute("scoreboard",scoreList);
            model.addAttribute("top10",scoreBoardService.getTop10());
            return "scoreboard/scoreboard";
        }
        
    }
    @GetMapping
    public String getScoreboard(Model model){
        List<ScoreSummary> scoreList= scoreBoardService.getScoreboardV2();
        log.info("scoreList size:"+scoreList.size());
        model.addAttribute("scoreboard",scoreList);
        model.addAttribute("top10",scoreBoardService.getTop10FromScoreboard(scoreList));
        return "scoreboard/scoreboard";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/publish")
    public String getPublish(Model model){
        model.addAttribute("publishList",challengeService.findAllManualChallenges());
        return "scoreboard/publish";
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/publish/challenge/{id}")
    public String publish(@PathVariable("id") Long id, RedirectAttributes redirectAttributes){

        ChallengeEntity challengeEntity = challengeService.findByID(id);

        String validateResult= scoreBoardService.validateBeforePublish(challengeEntity, redirectAttributes);
        if(validateResult!=null)
            return validateResult;

        scoreBoardService.publish(challengeEntity);

        redirectAttributes.addFlashAttribute("type", "success");
        redirectAttributes.addFlashAttribute("message", "Scoreboard has been updated!");
        return "redirect:/scoreboard";
    }


}
