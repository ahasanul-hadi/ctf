package com.cirt.ctf.challenge;

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

    public List<ChallengeEntity> getChallengesForAdmin() {
        List<ChallengeEntity> allChallenges = challengeRepository.findAll();
        return allChallenges;
    }
}
