package com.cirt.ctf.hints;

import com.cirt.ctf.exception.ApplicationException;
import com.cirt.ctf.team.TeamRepository;
import com.cirt.ctf.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Data
@Slf4j
public class HintsService {
    private final HintsRepository hintsRepository;
    private final TeamHintsRepository teamHintsRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public HintsEntity getHintById(Long id) throws ApplicationException {
        return hintsRepository.findById(id).orElseThrow(()-> new ApplicationException("No hint with id:"+id, HttpStatus.NOT_FOUND));
    }

    public Optional<TeamHintsEntity> getTeamHint(Long teamID, Long hintID) {
        return teamHintsRepository.findByTeamIdAndHintId(teamID, hintID);
    }

    @Transactional
    public void requestHint(Long teamID, Long hintID, Long requesterID) {
        HintsEntity hintEntity= hintsRepository.findById(hintID).orElseThrow();
        TeamHintsEntity teamHintsEntity= new TeamHintsEntity();
        teamHintsEntity.setHint(hintEntity);
        teamHintsEntity.setTeam(teamRepository.getReferenceById(teamID));
        teamHintsEntity.setPenalty(hintEntity.getDeductMark());
        teamHintsEntity.setRequester(userRepository.getReferenceById(requesterID));
        teamHintsEntity.setRequestTime(LocalDateTime.now());

        teamHintsRepository.save(teamHintsEntity);
    }
}
