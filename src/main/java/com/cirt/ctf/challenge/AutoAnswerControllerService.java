package com.cirt.ctf.challenge;

import java.security.Principal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cirt.ctf.user.User;
import com.cirt.ctf.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AutoAnswerControllerService {
    
    private final UserService userService;
    private final AutoAnswerRepository autoAnswerRepository;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/autoAnswer")
    public String getTeamwiseAnswerForAdmin(@RequestParam(name = "challenge") Long challengeId, @RequestParam(name = "team") Long teamId, Principal principal) {
        String answer = "NOT_AUTHORIZED";
        AutoAnswerEntity autoAnswerEntity;
        autoAnswerEntity = autoAnswerRepository.findByTeamIdAndChallengeId(teamId, challengeId);
        if(autoAnswerEntity == null) // no row found with selected team & challenge
        {
            answer = "";
        } else {
            answer = autoAnswerEntity.getAnswer();
        }
        return answer;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public AutoAnswerEntity getAutoAnswerRowForAdmin(@RequestParam(name = "challenge") Long challengeId, @RequestParam(name = "team") Long teamId) {
        AutoAnswerEntity autoAnswerEntity = null;
        autoAnswerEntity = autoAnswerRepository.findByTeamIdAndChallengeId(teamId, challengeId);
        return autoAnswerEntity;
    }

    public String getAutoAnswerForJudge(@RequestParam(name = "challenge") Long challengeId, @RequestParam(name = "team") Long teamId) {
        AutoAnswerEntity autoAnswerEntity = null;
        autoAnswerEntity = autoAnswerRepository.findByTeamIdAndChallengeId(teamId, challengeId);
        if(autoAnswerEntity == null) {
            log.error("No Auto-Answer found on DB!!!");
        }
        return autoAnswerEntity.getAnswer();
    }

    public String deleteSingleRowByAdmin(AutoAnswerEntity autoAnswerEntity) {
        
        this.autoAnswerRepository.delete(autoAnswerEntity);
        return "DELETED";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public int addAllRecords(List<AutoAnswerEntity> autoAnswerEntityList) {
        try {
            autoAnswerEntityList = this.autoAnswerRepository.saveAll(autoAnswerEntityList);
            return autoAnswerEntityList.size();
        } catch (Exception e) {
            log.error("addAllRecords(List<AutoAnswerEntity> autoAnswerEntityList) failed", e);
            return -1;
        }
        
    }
}
