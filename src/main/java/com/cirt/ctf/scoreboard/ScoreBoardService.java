package com.cirt.ctf.scoreboard;

import com.cirt.ctf.payload.CategoryBreakdown;
import com.cirt.ctf.payload.Top10Graph;
import com.cirt.ctf.submission.SubmissionRepository;
import com.cirt.ctf.team.TeamDTO;
import com.cirt.ctf.team.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ScoreBoardService {
    private final SubmissionRepository submissionRepository;
    private final TeamService teamService;

    public List<TeamDTO> getScoreboard(){

        List<TeamDTO> scoreboard= new ArrayList<>(teamService.findAll());

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

    public Integer getPlace(TeamDTO dto){
        List<TeamDTO> list= getScoreboard();
        for(int i=0;i<list.size();i++){
            if(list.get(i).getId()==dto.getId())
                return i+1;
        }
        return null;
    }

    public List<Top10Graph> getTop10(){
        List<TeamDTO>  top10= getScoreboard().stream().limit(10).toList();
        return top10.stream().map(dto->{
            return Top10Graph.builder()
                    .x(dto.getIncrementalScore().stream().map(inc->inc.getTime()).toList())
                    .y(dto.getIncrementalScore().stream().map(inc->inc.getScore()).toList())
                    .type("scatter")
                    .name(dto.getTeamName())
                    .build();

        }).toList();
    }

    public List<CategoryBreakdown> getCategoryWiseSolved(TeamDTO teamDTO){
        Map<String,CategoryBreakdown> map= new HashMap<>();
        submissionRepository.findByTeam(teamDTO.getId()).forEach(sub->{
            if(sub.getResult().getScore()>0){
                String category= sub.getChallenge().getCategory();
                CategoryBreakdown breakdown= map.getOrDefault(category, new CategoryBreakdown(category,0));
                breakdown.solved++;
                map.put(category, breakdown);
            }
        });
        return map.values().stream().toList();
    }

}
