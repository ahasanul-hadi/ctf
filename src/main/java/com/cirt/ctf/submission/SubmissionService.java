package com.cirt.ctf.submission;

import com.cirt.ctf.document.DocumentEntity;
import com.cirt.ctf.document.DocumentService;
import com.cirt.ctf.enums.Role;
import com.cirt.ctf.exception.ApplicationException;
import com.cirt.ctf.hints.HintsEntity;
import com.cirt.ctf.hints.HintsRepository;
import com.cirt.ctf.hints.TeamHintsEntity;
import com.cirt.ctf.hints.TeamHintsRepository;
import com.cirt.ctf.marking.ResultDTO;
import com.cirt.ctf.marking.ResultEntity;
import com.cirt.ctf.team.TeamRepository;
import com.cirt.ctf.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final ModelMapper modelMapper;
    private final DocumentService documentService;
    private final TeamHintsRepository teamHintsRepository;
    private final TeamRepository teamRepository;
    private final HintsRepository hintsRepository;

    public List<SubmissionDTO> findAll(){
        return submissionRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    public List<SubmissionEntity> findByChallenges(Long challengeID){
        return submissionRepository.findByChallengeId(challengeID);
    }


    public SubmissionDTO findById(Long id){
        return submissionRepository.findById(id).map(this::mapToDTO).orElseThrow();
    }

    public SubmissionEntity findEntityById(Long id) throws ApplicationException {
        return submissionRepository.findById(id).orElseThrow(()-> new ApplicationException("No Submission Found with id:"+id, HttpStatus.NOT_FOUND));
    }

    @Transactional
    public synchronized User bookExaminer(Long submissionID, User examiner) throws ApplicationException {
        if(examiner.getRole()!= Role.ADMIN)
            throw  new ApplicationException("Only Admin can give mark", HttpStatus.FORBIDDEN);

        SubmissionEntity submissionEntity = findEntityById(submissionID);
        if(submissionEntity.getTakenBy()!=null)
            return submissionEntity.getTakenBy();

        submissionEntity.setTakenBy(examiner);
        return submissionRepository.save(submissionEntity).getTakenBy();
    }

    public List<SubmissionDTO> findByTeam(Long teamID){
        return submissionRepository.findByTeam(teamID).stream().map(this::mapToDTO).toList();
    }


    public void giveMark(ResultDTO resultDTO){
        SubmissionEntity submissionEntity= submissionRepository.findById(resultDTO.getSubmissionID()).orElseThrow();
        ResultEntity resultEntity= submissionEntity.getResult();
        if(resultEntity==null)
            resultEntity= new ResultEntity();
        resultEntity.setScore(resultDTO.getScore());
        resultEntity.setComments(resultDTO.getComments());
        resultEntity.setExaminer(resultDTO.getExaminer());
        resultEntity.setMarkingTime(resultDTO.getMarkingTime());
        resultEntity.setSubmission(submissionEntity);
        submissionEntity.setResult(resultEntity);


        submissionEntity.setMark(resultDTO.getScore());
        //Integer hintPenalty = teamHintsRepository.findByTeamIdAndHintId(submissionEntity.getTeam().getId(), submissionEntity.getChallenge().getId()).map(e->e.getHint().getDeductMark()).orElse(0);
        //submissionEntity.setPenalty(hintPenalty);
        int penalty= submissionEntity.getPenalty()==null? 0: submissionEntity.getPenalty();
        submissionEntity.setScore(submissionEntity.getMark() - penalty);

        //only verified when score is published
        if(submissionEntity.getChallenge().isScoreboardPublished()){
            submissionEntity.setPublished(true);
        }
        submissionRepository.save(submissionEntity);

    }

    public SubmissionEntity createSubmission(SubmissionDTO submissionDTO){
        Optional<SubmissionEntity> optionalSubmission= submissionRepository.getSubmissionByTeamAndChallenge(submissionDTO.getTeam().getId(), submissionDTO.getChallenge().getId());
        SubmissionEntity submissionEntity = optionalSubmission.orElse(new SubmissionEntity());
        submissionEntity.setChallenge(submissionDTO.getChallenge());
        submissionEntity.setSolver(submissionDTO.getSolver());
        submissionEntity.setTeam(submissionDTO.getTeam());
        submissionEntity.setSubmissionTime(submissionDTO.getSubmissionTime());
        submissionEntity.setAttemptCount(submissionEntity.getAttemptCount()+1);
        if((submissionDTO.getFile() != null && submissionDTO.getFile().getSize() > 0) && submissionDTO.getChallenge().getMarkingType().equals("manual")) {
            DocumentEntity documentEntity = documentService.saveDocument(submissionDTO.getFile());
            submissionEntity.setDocumentID(documentEntity.getId());
        } else {
            submissionEntity.setDocumentID(submissionDTO.getDocumentID());
        }
        return submissionRepository.save(submissionEntity);
    }

    public Integer getSubmissionCount(Long teamID, Long challengeID){
        //return submissionRepository.getSubmissionListByChallengeAndTeam(teamID,challengeID).size();
        Optional<SubmissionEntity> optionalSubmissionEntity = submissionRepository.getSubmissionByTeamAndChallenge(teamID, challengeID);
        return optionalSubmissionEntity.map(SubmissionEntity::getAttemptCount).orElse(0);
    }

    public boolean anySubmissionAccepted(Long teamID, Long challengeID) {
        boolean isACCEPTED = false;
        List<SubmissionEntity> submissionEntities = submissionRepository.getSubmissionListByChallengeAndTeam(teamID, challengeID);

        for(SubmissionEntity submissionEntity: submissionEntities) {
            if(submissionEntity.getMark()!=null && submissionEntity.getMark()>0) {
                isACCEPTED = true;
                break;
            }
        }
        return isACCEPTED;
    }

    public SubmissionDTO mapToDTO(SubmissionEntity entity){
        SubmissionDTO submissionDTO= modelMapper.map(entity, SubmissionDTO.class);
        String absPath = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/document/preview/{id}")
                .buildAndExpand(entity.getDocumentID())
                .toUriString();
        submissionDTO.setFilePath(absPath);

        return submissionDTO;
    }

    public List<SubmissionEntity> findAllSubmissions() {
        return submissionRepository.findAllSubmissions();
    }

    public void submitPenalty(Long teamID, User requester, HintsEntity hint) {
        Optional<SubmissionEntity> optionalSubmissionEntity =submissionRepository.getSubmissionByTeamAndChallenge(teamID, hint.getChallenge().getId());
        SubmissionEntity submissionEntity= optionalSubmissionEntity.orElseGet(()->{
            SubmissionEntity entity= new SubmissionEntity();
            entity.setTeam(teamRepository.getReferenceById(teamID));
            entity.setChallenge(hint.getChallenge());
            entity.setSubmissionTime(LocalDateTime.now());
            entity.setSolver(requester);
            if(hint.getChallenge().isScoreboardPublished())
                entity.setPublished(true);
            entity.setMark(null);
            return entity;
        });
        ResultEntity resultEntity = submissionEntity.getResult() ==null? new ResultEntity() : submissionEntity.getResult();
        resultEntity.setSubmission(submissionEntity);
        resultEntity.setMarkingTime(LocalDateTime.now());
        resultEntity.setScore(hint.getDeductMark()*-1);
        resultEntity.setComments("PENALTY");
        submissionEntity.setResult(resultEntity);


        int mark= submissionEntity.getMark()==null? 0: submissionEntity.getMark();
        submissionEntity.setPenalty(hint.getDeductMark());
        submissionEntity.setScore(mark - submissionEntity.getPenalty());

        submissionRepository.save(submissionEntity);
    }
}
