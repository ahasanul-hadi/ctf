package com.cirt.ctf.submission;

import com.cirt.ctf.document.DocumentEntity;
import com.cirt.ctf.document.DocumentService;
import com.cirt.ctf.enums.Role;
import com.cirt.ctf.exception.ApplicationException;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final ModelMapper modelMapper;
    private final DocumentService documentService;

    public List<SubmissionDTO> findAll(){
        return submissionRepository.findAll().stream().map(this::mapToDTO).toList();
    }
    public List<SubmissionDTO> findByChallenges(Long challengeID){
        return submissionRepository.findByChallengeId(challengeID).stream().map(this::mapToDTO).toList();
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
        ResultEntity resultEntity= ResultEntity.builder()
                .score(resultDTO.getScore())
                .comments(resultDTO.getComments())
                .examiner(resultDTO.getExaminer())
                .markingTime(resultDTO.getMarkingTime())
                .submission(submissionEntity).build();

        submissionEntity.setResult(resultEntity);


        //only verified when score is published
        //submissionEntity.setPublished(true);
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
        if((submissionDTO.getFile() != null && submissionDTO.getFile().getSize() > 0) || submissionDTO.getChallenge().getMarkingType().equals("manual")) {
            try {
                DocumentEntity documentEntity = documentService.saveDocument(submissionDTO.getFile());
                submissionEntity.setDocumentID(documentEntity.getId());
            } catch (Exception ignored){}
        } else {
            submissionEntity.setDocumentID(submissionDTO.getDocumentID());
        }
        SubmissionEntity returned;
        try {
            returned = submissionRepository.save(submissionEntity);
        } catch(Exception e) {
            throw e;
        }

        return returned;
    }

    public Integer getSubmissionCount(Long teamID, Long challengeID){
        return submissionRepository.getSubmissionListByChallengeAndTeam(teamID,challengeID).size();
    }

    public boolean anySubmissionAccepted(Long teamID, Long challengeID) {
        boolean isACCEPTED = false;
        List<SubmissionEntity> submissionEntities = submissionRepository.getSubmissionListByChallengeAndTeam(teamID, challengeID);

        for(SubmissionEntity submissionEntity: submissionEntities) {
            if(submissionEntity.getResult().getComments().equals("ACCEPTED")) {
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
}
