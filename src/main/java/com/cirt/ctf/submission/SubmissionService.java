package com.cirt.ctf.submission;

import com.cirt.ctf.document.DocumentEntity;
import com.cirt.ctf.document.DocumentService;
import com.cirt.ctf.marking.ResultDTO;
import com.cirt.ctf.marking.ResultEntity;
import com.cirt.ctf.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final ModelMapper modelMapper;
    private final DocumentService documentService;

    public List<SubmissionDTO> findAll(){
        return submissionRepository.findAll().stream().map(e->modelMapper.map(e,SubmissionDTO.class)).toList();
    }

    public SubmissionDTO findById(Long id){
        return submissionRepository.findById(id).map(e->modelMapper.map(e,SubmissionDTO.class)).orElseThrow();
    }

    public List<SubmissionDTO> findByTeam(Long teamID){
        return submissionRepository.findByTeam(teamID).stream().map(e->modelMapper.map(e,SubmissionDTO.class)).toList();
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
        if(submissionDTO.getFile() != null && submissionDTO.getFile().getSize() > 0 ) {
            try {
                DocumentEntity documentEntity = documentService.saveDocument(submissionDTO.getFile());
                submissionEntity.setDocumentID(documentEntity.getId());
            } catch (Exception ignored){}
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

}
