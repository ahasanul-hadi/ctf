package com.cirt.ctf.submission;

import com.cirt.ctf.marking.ResultDTO;
import com.cirt.ctf.marking.ResultEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final ModelMapper modelMapper;

    public List<SubmissionDTO> findAll(){
        return submissionRepository.findAll().stream().map(e->modelMapper.map(e,SubmissionDTO.class)).toList();
    }

    public SubmissionDTO findById(Long id){
        return submissionRepository.findById(id).map(e->modelMapper.map(e,SubmissionDTO.class)).orElseThrow();
    }

    public SubmissionDTO giveMark(ResultDTO resultDTO){
        SubmissionEntity submissionEntity= submissionRepository.findById(resultDTO.getSubmissionID()).orElseThrow();
        ResultEntity resultEntity= ResultEntity.builder()
                .score(resultDTO.getScore())
                .comments(resultDTO.getComments())
                .examiner(resultDTO.getExaminer())
                .markingTime(resultDTO.getMarkingTime())
                .submission(submissionEntity).build();

        submissionEntity.setResult(resultEntity);
        submissionEntity.setVerified(true);

        submissionEntity= submissionRepository.save(submissionEntity);

        return modelMapper.map(submissionEntity, SubmissionDTO.class);
    }
}
