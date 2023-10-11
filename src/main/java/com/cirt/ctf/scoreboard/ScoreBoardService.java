package com.cirt.ctf.scoreboard;

import com.cirt.ctf.payload.Top10Graph;
import com.cirt.ctf.submission.SubmissionRepository;
import com.cirt.ctf.team.TeamDTO;
import com.cirt.ctf.team.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
                if (score1 == score2)
                    return first.getLastSubmissionTime().compareTo(second.getLastSubmissionTime());
                else
                    return score2 - score1;

            }
        });
        return scoreboard;

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

}
