package com.cirt.ctf.challenge;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.cirt.ctf.hints.HintsDTO;
import com.cirt.ctf.hints.HintsEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


import lombok.Data;
import lombok.RequiredArgsConstructor;

@Service
@Data
@RequiredArgsConstructor
public class ChallengeService {
    private final ChallengeRepository challengeRepository;
    private final AutoAnswerRepository autoAnswerRepository;
    private final ModelMapper modelMapper;

    public ChallengeEntity getChallengeById(long id) {
        return challengeRepository.findById(id).orElseThrow();
    }
    public List<ChallengeDTO> getChallengesForAdmin() {
        List<ChallengeEntity> challengeEntities = challengeRepository.findAll();
        List<ChallengeDTO> challengeDTOs = new ArrayList<>();
        System.out.println(challengeEntities.size());
        for(ChallengeEntity challengeEntity: challengeEntities) {
            ChallengeDTO dto = new ChallengeDTO();
            dto.setId(challengeEntity.getId());
            dto.setTitle(challengeEntity.getTitle());
            dto.setCategory(challengeEntity.getCategory());
            dto.setTotalMark(challengeEntity.getTotalMark());
            dto.setVisibility(challengeEntity.getVisibility());
            challengeDTOs.add(dto);
        }
        System.out.println(challengeDTOs.size());
        return challengeDTOs;
    }

    public List<ChallengeDTO> getChallengesForUser() {
        List<ChallengeEntity> challengeEntities = challengeRepository.findByVisibility("public");
        List<ChallengeDTO> challengeDTOs = new ArrayList<>();
        for(ChallengeEntity challengeEntity: challengeEntities) {
            ChallengeDTO dto = new ChallengeDTO();
            dto.setId(challengeEntity.getId());
            dto.setTitle(challengeEntity.getTitle());
            dto.setCategory(challengeEntity.getCategory());
            dto.setDescription(challengeEntity.getDescription());
            dto.setTotalMark(challengeEntity.getTotalMark());
            dto.setVisibility(challengeEntity.getVisibility());
            dto.setDeadline(challengeEntity.getDeadline().toString());
            dto.setAttempts(challengeEntity.getAttempts());
            dto.setMarkingType(challengeEntity.getMarkingType());
            dto.setHint(modelMapper.map(challengeEntity.getHint(), HintsDTO.class));
            challengeDTOs.add(dto);
        }
        return challengeDTOs;
    }

    public ChallengeEntity saveChallenge(ChallengeDTO challengeDTO) {
        Date date = null;
        try {
            date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm")).parse(challengeDTO.getDeadline());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ChallengeEntity challengeEntity = new ChallengeEntity();
        challengeEntity.setTitle(challengeDTO.getTitle());
        challengeEntity.setCategory(challengeDTO.getCategory());
        challengeEntity.setTotalMark(challengeDTO.getTotalMark());
        challengeEntity.setVisibility(challengeDTO.getVisibility());
        challengeEntity.setMarkingType(challengeDTO.getMarkingType());
        challengeEntity.setDeadline(date.toInstant().atZone(ZoneId.of("Asia/Dhaka")).toLocalDateTime());
        challengeEntity.setAttempts(challengeDTO.getAttempts());
        challengeEntity.setDescription(challengeDTO.getDescription());
        challengeEntity.setScoreboardPublished(challengeDTO.getMarkingType().equals("auto") ? true : false);
        
        HintsEntity hint= modelMapper.map(challengeDTO.getHint(), HintsEntity.class);
        hint.setChallenge(challengeEntity);
        challengeEntity.setHint(hint);

        try {
            challengeEntity = this.challengeRepository.save(challengeEntity);
        } catch (Exception e) {
            throw e;
        }
        return challengeEntity;
    }

    public ChallengeEntity saveChallengeFromExcel(ChallengeDTO challengeDTO) {
        Date date = null;
        try {
            date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm")).parse(challengeDTO.getDeadline());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ChallengeEntity challengeEntity = new ChallengeEntity();
        challengeEntity.setId(challengeDTO.getId());
        challengeEntity.setTitle(challengeDTO.getTitle());
        challengeEntity.setCategory(challengeDTO.getCategory());
        challengeEntity.setTotalMark(challengeDTO.getTotalMark());
        challengeEntity.setVisibility(challengeDTO.getVisibility());
        challengeEntity.setMarkingType(challengeDTO.getMarkingType());
        challengeEntity.setDeadline(date.toInstant().atZone(ZoneId.of("Asia/Dhaka")).toLocalDateTime());
        challengeEntity.setAttempts(challengeDTO.getAttempts());
        challengeEntity.setDescription(challengeDTO.getDescription());
        challengeEntity.setScoreboardPublished(challengeDTO.getMarkingType().equals("auto") ? true : false);
        
        HintsEntity hint= modelMapper.map(challengeDTO.getHint(), HintsEntity.class);
        hint.setChallenge(challengeEntity);
        challengeEntity.setHint(hint);

        try {
            challengeEntity = this.challengeRepository.save(challengeEntity);
        } catch (Exception e) {
            throw e;
        }
        return challengeEntity;
    }

    public ChallengeEntity findByID(Long id){
        return challengeRepository.findById(id).orElse(null);
    }

    public List<ChallengeDTO> findAll(){
        return challengeRepository.findAll().stream().map(entity->modelMapper.map(entity,ChallengeDTO.class)).toList();
    }

    public List<ChallengeDTO> findAllManualChallenges(){
        return challengeRepository.findByMarkingType("manual").stream().map(entity->modelMapper.map(entity,ChallengeDTO.class)).toList();
    }

    public ChallengeEntity updateChallenge(Long id, ChallengeDTO challengeDTO) {
        ChallengeEntity challengeEntity = challengeRepository.findById(id).orElseThrow();
        Date date = null;
        try {
            date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm")).parse(challengeDTO.getDeadline());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        challengeEntity.setTitle(challengeDTO.getTitle());
        challengeEntity.setCategory(challengeDTO.getCategory());
        challengeEntity.setDescription(challengeDTO.getDescription());
        challengeEntity.setTotalMark(challengeDTO.getTotalMark());
        challengeEntity.setDeadline(date.toInstant().atZone(ZoneId.of("Asia/Dhaka")).toLocalDateTime());
        challengeEntity.setVisibility(challengeDTO.getVisibility());
        //challengeEntity.setMarkingType(challengeDTO.getMarkingType());
        challengeEntity.setAttempts(challengeDTO.getAttempts());
        challengeEntity.getHint().setDescription(challengeDTO.getHint().getDescription());
        challengeEntity.getHint().setDeductMark(challengeDTO.getHint().getDeductMark());
        challengeEntity.getHint().setShowHint(challengeDTO.getHint().isShowHint());
        challengeRepository.save(challengeEntity);
        return challengeEntity;
    }

    public ChallengeEntity save(ChallengeEntity entity){
        return challengeRepository.save(entity);
    }

    public void delete(Long id) {
        challengeRepository.deleteById(id);
    }

    public Long getChallengeCount(){
        return challengeRepository.count();
    }
}
