package com.cirt.ctf.challenge;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


import lombok.Data;
import lombok.RequiredArgsConstructor;

@Service
@Data
@RequiredArgsConstructor
public class ChallengeService {
    private final ChallengeRepository challengeRepository;
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

        try {
            challengeEntity = this.challengeRepository.save(challengeEntity);
        }catch (Exception e) {
            throw e;
        }
        return challengeEntity;
    }

    public List<ChallengeDTO> findAll(){
        return challengeRepository.findAll().stream().map(entity->modelMapper.map(entity,ChallengeDTO.class)).toList();
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
        challengeEntity.setMarkingType(challengeDTO.getMarkingType());
        challengeRepository.save(challengeEntity);
        return challengeEntity;
    }
}
