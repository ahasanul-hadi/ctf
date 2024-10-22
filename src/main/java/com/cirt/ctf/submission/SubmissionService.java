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
import java.util.*;

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
        submissionEntity.setScore(resultDTO.getScore());
        //only verified when score is published
        if(submissionEntity.getChallenge().isScoreboardPublished()){
            submissionEntity.setPublished(true);
        }
        submissionRepository.save(submissionEntity);

    }

    public SubmissionEntity createSubmission(SubmissionDTO submissionDTO){
        SubmissionEntity submissionEntity = new SubmissionEntity();
        submissionEntity.setChallenge(submissionDTO.getChallenge());
        submissionEntity.setSolver(submissionDTO.getSolver());
        submissionEntity.setTeam(submissionDTO.getTeam());
        submissionEntity.setSubmissionTime(submissionDTO.getSubmissionTime());
        submissionEntity.setSubmissionType(submissionDTO.getSubmissionType());
        if((submissionDTO.getFile() != null && submissionDTO.getFile().getSize() > 0) && submissionDTO.getChallenge().getMarkingType().equals("manual")) {
            DocumentEntity documentEntity = documentService.saveDocument(submissionDTO.getFile());
            submissionEntity.setDocumentID(documentEntity.getId());
        } else {
            submissionEntity.setDocumentID(submissionDTO.getDocumentID());
        }
        return submissionRepository.save(submissionEntity);
    }

    public Integer getSubmissionCount(Long teamID, Long challengeID){
        return submissionRepository.getAttemptCountByTeamAndChallenge(teamID,challengeID);
        //Optional<SubmissionEntity> optionalSubmissionEntity = submissionRepository.getSubmissionByTeamAndChallenge(teamID, challengeID);
        //return optionalSubmissionEntity.map(SubmissionEntity::getAttemptCount).orElse(0);
    }

    public boolean anySubmissionAccepted(Long teamID, Long challengeID) {
        List<SubmissionEntity> submissionEntities = submissionRepository.getSubmissionListByChallengeAndTeam(teamID, challengeID);
        for(SubmissionEntity submissionEntity: submissionEntities) {
            if(submissionEntity.getMark()!=null && submissionEntity.getMark()>0) {
                return true;
            }
        }
        return false;
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
        SubmissionEntity submissionEntity= new SubmissionEntity();
        submissionEntity.setTeam(teamRepository.getReferenceById(teamID));
        submissionEntity.setChallenge(hint.getChallenge());
        submissionEntity.setSubmissionTime(LocalDateTime.now());
        submissionEntity.setSolver(requester);
        if(hint.getChallenge().isScoreboardPublished())
            submissionEntity.setPublished(true);
        submissionEntity.setPenalty(hint.getDeductMark());
        submissionEntity.setMark(null);
        submissionEntity.setScore(hint.getDeductMark()*-1);


        ResultEntity resultEntity = new ResultEntity();
        resultEntity.setSubmission(submissionEntity);
        resultEntity.setMarkingTime(LocalDateTime.now());
        resultEntity.setScore(hint.getDeductMark()*-1);
        resultEntity.setComments("PENALTY");
        submissionEntity.setResult(resultEntity);

        submissionRepository.save(submissionEntity);
    }

    public List<SubmissionDTO> getSubmissionSummary(Long teamID){
        List<SubmissionEntity> submissions= submissionRepository.findByTeam(teamID).stream().sorted(Comparator.comparing(SubmissionEntity::getSubmissionTime).reversed()).toList();
        Map<Long, SubmissionDTO> map= new HashMap<>();
        submissions.forEach(e->{
            SubmissionDTO dto= map.get(e.getChallenge().getId());
            if(dto==null) {
                dto = modelMapper.map(e, SubmissionDTO.class);
                dto.setScore(0);
                map.put(e.getChallenge().getId(), dto);
            }
            dto.setScore(dto.getScore() + e.getScore());
        });
        return map.values().stream().toList();
    }

}
