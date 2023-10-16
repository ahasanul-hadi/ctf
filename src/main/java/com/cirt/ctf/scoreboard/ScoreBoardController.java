package com.cirt.ctf.scoreboard;

import com.cirt.ctf.challenge.ChallengeService;
import com.cirt.ctf.team.TeamDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping("/publish")
    public String getPublish(Model model){
        model.addAttribute("scoreboard",challengeService.findAll());
        model.addAttribute("top10",scoreBoardService.getTop10());
        return "scoreboard/scoreboard";
    }


    @PostMapping("/publish")
    public String publish(Model model){
        model.addAttribute("scoreboard",scoreBoardService.getScoreboard());
        model.addAttribute("top10",scoreBoardService.getTop10());
        return "scoreboard/scoreboard";
    }


}
