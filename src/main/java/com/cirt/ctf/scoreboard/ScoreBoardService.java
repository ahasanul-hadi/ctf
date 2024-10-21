package com.cirt.ctf.scoreboard;

import com.cirt.ctf.challenge.ChallengeDTO;
import com.cirt.ctf.challenge.ChallengeEntity;
import com.cirt.ctf.challenge.ChallengeService;
import com.cirt.ctf.payload.CategoryBreakdown;
import com.cirt.ctf.payload.IncrementalScore;
import com.cirt.ctf.payload.Top10Graph;
import com.cirt.ctf.submission.SubmissionEntity;
import com.cirt.ctf.submission.SubmissionRepository;
import com.cirt.ctf.team.TeamDTO;
import com.cirt.ctf.team.TeamEntity;
import com.cirt.ctf.team.TeamService;
import com.cirt.ctf.util.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoreBoardService {
    private final SubmissionRepository submissionRepository;
    private final TeamService teamService;
    private final ChallengeService challengeService;

    /*
    public List<TeamDTO> getScoreboard(){
        List<TeamDTO> scoreboard= new ArrayList<>(teamService.findWithoutAdminTeam());
        scoreboard.sort(new Comparator<TeamDTO>() {
            public int compare(TeamDTO first, TeamDTO second) {
                int score1 = first.getScore();
                int score2 = second.getScore();
                if (score1 == score2){
                    LocalDateTime firstSubmission=first.getLastSubmissionTime();
                    LocalDateTime secondSubmission=second.getLastSubmissionTime();
                    if(firstSubmission!=null && secondSubmission!=null){
                        return firstSubmission.compareTo(secondSubmission);
                    }
                    else if(firstSubmission==null && secondSubmission==null)
                        return (int) (first.getId().compareTo(second.getId()));
                    else if(firstSubmission==null)
                        return 1;
                    else
                        return -1;
                }
                else
                    return score2 - score1;

            }
        });
        return scoreboard;
    }
     */

    public List<ScoreSummary> getScoreboardV2(){
        return submissionRepository.getScoreboardV2();
    }

    public String getPlace(Long teamID){
        List<ScoreSummary>  list= getScoreboardV2();
        for(int i=0;i<list.size();i++){
            if(Objects.equals(list.get(i).getId(), teamID))
                return Integer.toString(i+1);
        }
        return "-";
    }

    /*
    public List<Top10Graph> getTop10(){
        List<TeamDTO>  top10= getScoreboard().stream().limit(10).toList();
        return top10.stream().map(dto->{
            return Top10Graph.builder()
                    .x(getIncrementalScore(dto.getSubmissions()).stream().map(IncrementalScore::getTime).toList())
                    .y(getIncrementalScore(dto.getSubmissions()).stream().map(IncrementalScore::getScore).toList())
                    .type("scatter")
                    .name(dto.getTeamName())
                    .build();

        }).toList();
    }
    */


    public List<Top10Graph> getTop10FromScoreboard( List<ScoreSummary> scoreList){
        List<Long>  top10Ids= scoreList.stream().map(ScoreSummary::getId).limit(10).toList();
        return teamService.findByIds(top10Ids).stream().map(team->{
            return Top10Graph.builder()
                    .x(getIncrementalScore(team.getSubmissions()).stream().map(IncrementalScore::getTime).toList())
                    .y(getIncrementalScore(team.getSubmissions()).stream().map(IncrementalScore::getScore).toList())
                    .type("scatter")
                    .name(team.getTeamName())
                    .build();

        }).toList();
    }

    public List<IncrementalScore> getIncrementalScore(List<SubmissionEntity> submissions){
        List<IncrementalScore> list= new ArrayList<>();
        List<SubmissionEntity> orderSubmissions= new ArrayList<>(submissions).stream().filter(sub->sub.isPublished() && sub.getScore()>0).sorted(Comparator.comparing(SubmissionEntity::getSubmissionTime)).toList();
        int gradualScore=0;
        for(SubmissionEntity e: orderSubmissions){
            gradualScore+=e.getScore();
            IncrementalScore ic= IncrementalScore.builder()
                    .score(gradualScore)
                    .time(e.getSubmissionTime().format(Utils.formatter)).build();
            list.add(ic);

        }
        return list;

    }

    public List<CategoryBreakdown> getCategoryWiseSolved(TeamDTO teamDTO){
        Map<String,CategoryBreakdown> map= new HashMap<>();
        submissionRepository.findByTeam(teamDTO.getId()).forEach(sub->{
            if(sub.isPublished() && sub.getScore()>0){
                String category= sub.getChallenge().getCategory();
                CategoryBreakdown breakdown= map.getOrDefault(category, new CategoryBreakdown(category,0));
                breakdown.solved++;
                map.put(category, breakdown);
            }
        });
        return map.values().stream().toList();
    }

    @Transactional
    public void publish(ChallengeEntity challengeEntity) {
        challengeEntity.setScoreboardPublished(true);

        challengeEntity=challengeService.save(challengeEntity);

        challengeEntity.getSubmissions().forEach(submissionEntity -> {
            if(submissionEntity.getResult()!=null){
                submissionEntity.setPublished(true);
                submissionRepository.save(submissionEntity);
            }
        });
    }

    public String validateBeforePublish(ChallengeEntity challengeEntity, RedirectAttributes redirectAttributes){
        if(challengeEntity.getDeadline().isAfter(LocalDateTime.now())){
            redirectAttributes.addFlashAttribute("type", "error");
            redirectAttributes.addFlashAttribute("message", "Deadline has not been ended for this challenge");
            return "redirect:/marking/challenges/"+challengeEntity.getId();
        }
        List<SubmissionEntity> submissionEntityList= challengeEntity.getSubmissions();
        for(SubmissionEntity submissionEntity: submissionEntityList){
            if(submissionEntity.getResult()==null){
                redirectAttributes.addFlashAttribute("type", "error");
                redirectAttributes.addFlashAttribute("message", "All submissions for "+challengeEntity.getTitle()+" has not been assessed yet!");
                return "redirect:/marking/challenges/"+challengeEntity.getId();
            }
        }

        return null;
    }

}
