package com.cirt.ctf.challenge;

import java.util.List;

import com.cirt.ctf.team.TeamEntity;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class AutoAnswerDTO {
    private long id;

    private Long teamId;

    private Long challengeId;

    private String answer;
}
