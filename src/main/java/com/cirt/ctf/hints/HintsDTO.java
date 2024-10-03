package com.cirt.ctf.hints;

import com.cirt.ctf.challenge.ChallengeEntity;
import com.cirt.ctf.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class HintsDTO {
    private long id;
    private ChallengeEntity challenge;
    private Integer deductMark;
    private String description;
    private boolean showHint= false;
}
