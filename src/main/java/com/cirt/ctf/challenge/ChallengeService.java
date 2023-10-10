package com.cirt.ctf.challenge;

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
}
